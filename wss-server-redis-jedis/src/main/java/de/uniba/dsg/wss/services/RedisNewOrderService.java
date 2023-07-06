package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponseItem;
import de.uniba.dsg.wss.service.NewOrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Implemented similarly to MsNewOrderService of Ms-Sync */
@Service
public class RedisNewOrderService extends NewOrderService {

  private static final Logger LOG = LogManager.getLogger(RedisNewOrderService.class);
  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final ProductRepository productRepository;
  private final StockRepository stockRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final OrderItemRepository orderItemRepository;
  public static int maxRetries = 5;

  @Autowired
  public RedisNewOrderService(
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      ProductRepository productRepository,
      StockRepository stockRepository,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      OrderItemRepository orderItemRepository) {
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.productRepository = productRepository;
    this.stockRepository = stockRepository;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.orderItemRepository = orderItemRepository;
  }

  /*
   TODO: Need to figure out how it will behave in concurrent condition
  */

  @Override
  public NewOrderResponse process(NewOrderRequest newOrderRequest) {
    OrderData storedOrder = null;

    // TODO: keeping it until concurrency testing
    //    for (int i = 0; i < maxRetries; i++) {
    //      try {
    //        // synchronized access
    //        storedOrder = processOrderRequest(req);
    //        break;
    //      } catch (MsTransactionException e) {
    //        // TODO handle exception?
    //      }
    //    }

    WarehouseData warehouseData = warehouseRepository.findById(newOrderRequest.getWarehouseId());
    CustomerData customerData = customerRepository.findById(newOrderRequest.getCustomerId());

    if (warehouseData == null || customerData == null) {
      throw new IllegalArgumentException();
    }

    DistrictData districtData = districtRepository.findById(newOrderRequest.getDistrictId());
    if (districtData == null) {
      throw new IllegalArgumentException();
    }

    List<StockUpdateDto> stockUpdates = new ArrayList<>();
    boolean allLocal = true;

    for (NewOrderRequestItem item : newOrderRequest.getItems()) {
      // TODO: Possible improvement when calling the id of stockData: by productId +
      // getSupplyingWarehouseId() in model and converter
      // TODO: Would also reduce the amount of calls to server
      StockData stockData =
          findByWarehouseIdAndProductId(item.getProductId(), item.getSupplyingWarehouseId());
      if (stockData == null) {
        throw new IllegalArgumentException();
      }
      if (!newOrderRequest.getWarehouseId().equals(stockData.getWarehouseRefId())) {
        allLocal = false;
      }
      StockUpdateDto stockUpdate = new StockUpdateDto(stockData, item.getQuantity());
      stockUpdates.add(stockUpdate);
    }

    // Creation of new order
    OrderData order =
        new OrderData(
            districtData.getId(),
            customerData.getId(),
            LocalDateTime.now(),
            stockUpdates.size(),
            allLocal);
    storedOrder = storeOrder(order, stockUpdates);

    return getNewOrderResponse(newOrderRequest, storedOrder);
  }

  private NewOrderResponse getNewOrderResponse(
      NewOrderRequest newOrderRequest, OrderData storedOrder) {
    if (storedOrder == null) {
      LOG.info("Cancel order processing");
      throw new RedisTransactionException("Order is not processable");
    }

    double orderItemSum = 0;
    // creation of return dtos
    List<NewOrderResponseItem> dtoItems = new ArrayList<>();

    for (String orderItemId : storedOrder.getItemsIds()) {
      OrderItemData orderItemData = orderItemRepository.findById(orderItemId);
      ProductData productData = productRepository.findById(orderItemData.getProductRefId());
      dtoItems.add(
          new NewOrderResponseItem(
              orderItemData.getSupplyingWarehouseRefId(),
              productData.getId(),
              productData.getName(),
              productData.getPrice(),
              orderItemData.getAmount(),
              orderItemData.getQuantity(),
              orderItemData.getLeftQuantityInStock(),
              determineBrandGeneric(productData.getData(), "stock data")));
      orderItemSum += orderItemData.getAmount();
    }

    DistrictData districtData = districtRepository.findById(storedOrder.getDistrictRefId());
    WarehouseData warehouse = warehouseRepository.findById(districtData.getWarehouseRefId());
    CustomerData customerData = customerRepository.findById(storedOrder.getCustomerRefId());

    NewOrderResponse newOrderResponse =
        newOrderResponse(
            newOrderRequest,
            storedOrder.getId(),
            storedOrder.getEntryDate(),
            warehouse.getSalesTax(),
            districtData.getSalesTax(),
            customerData.getCredit(),
            customerData.getDiscount(),
            customerData.getLastName());
    newOrderResponse.setTotalAmount(
        calcOrderTotal(
            orderItemSum,
            customerData.getDiscount(),
            warehouse.getSalesTax(),
            districtData.getSalesTax()));

    newOrderResponse.setOrderItems(dtoItems);
    return newOrderResponse;
  }

  private OrderData storeOrder(OrderData order, List<StockUpdateDto> stockUpdates)
      throws RedisTransactionException {
    List<OrderItemData> itemDataList = updateStock(order, stockUpdates);
    if (itemDataList.isEmpty()) {
      throw new RedisTransactionException("Order item update failed");
    }

    for (OrderItemData orderItem : itemDataList) {
      order.getItemsIds().add(orderItem.getId());
    }

    orderRepository.save(order);
    OrderData orderExampleForFetchToThrowError = orderRepository.findById(order.getId());
    if (!orderExampleForFetchToThrowError.equals(order)) LOG.debug("Not equal");

    return order;
  }

  private List<OrderItemData> updateStock(OrderData order, List<StockUpdateDto> stockUpdates) {
    List<OrderItemData> orderItemsList = new ArrayList<>();
    for (int i = 0; i < stockUpdates.size(); i++) {
      StockUpdateDto stockUpdateDto = stockUpdates.get(i);
      if (!stockUpdateDto.getStockData().reduceQuantity(stockUpdateDto.getQuantity())) {
        break;
      } else {
        ProductData productData =
            productRepository.findById(stockUpdateDto.getStockData().getProductRefId());
        OrderItemData orderItem =
            new OrderItemData(
                order.getId(),
                stockUpdateDto.getStockData().getProductRefId(),
                stockUpdateDto.getStockData().getWarehouseRefId(),
                i,
                stockUpdateDto.getQuantity(),
                stockUpdateDto.getStockData().getQuantity(),
                stockUpdateDto.getQuantity() * productData.getPrice(),
                stockUpdateDto.getStockData().getDist01());
        // TODO: Einzeln abspeichern oder in BATCH ?
        orderItemRepository.save(orderItem);
        orderItemsList.add(orderItem);
      }
    }

    //        // compensate the first transactions, if some updates fail
    //        if (i != stockUpdates.size()) {
    //          for (int j = 0; j < i; j++) {
    //            StockUpdateDto stockUpdate = stockUpdates.get(j);
    //            stockUpdate.getStockData().undoReduceQuantityOperation(stockUpdate.getQuantity());
    //          }
    //          return List.of();
    //        }
    return orderItemsList;
  }

  private StockData findByWarehouseIdAndProductId(String productId, String supplyingWarehouseId) {
    WarehouseData warehouseData = warehouseRepository.findById(supplyingWarehouseId);
    ProductData productData = productRepository.findById(productId);
    // TODO: DO THE BATCH-CALL Instead
    for (String stockId : warehouseData.getStockRefsIds()) {
      StockData stockData = stockRepository.findById(stockId);
      if (stockData.getProductRefId().equals(productData.getId())) {
        return stockData;
      }
    }
    return null;
  }
}

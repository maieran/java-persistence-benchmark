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
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link NewOrderService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 * @author Andre Maier
 */
@Service
public class AerospikeNewOrderService extends NewOrderService {
  private static final Logger LOG = LogManager.getLogger(AerospikeNewOrderService.class);
  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final ProductRepository productRepository;
  private final StockRepository stockRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final OrderItemRepository orderItemRepository;
  public static int maxRetries = 5;

  @Autowired
  public AerospikeNewOrderService(
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

  @Override
  public NewOrderResponse process(NewOrderRequest newOrderRequest) {
    OrderData storedOrder = null;
    for (int i = 0; i < maxRetries; i++) {
      try {
        storedOrder = processOrderRequest(newOrderRequest);
        break;
      } catch (AerospikeTransactionException e) {

      }
    }

    return getNewOrderResponse(newOrderRequest, storedOrder);
  }

  private OrderData processOrderRequest(NewOrderRequest newOrderRequest) {
    OrderData storedOrder;
    Optional<WarehouseData> warehouseData =
        warehouseRepository.findById(newOrderRequest.getWarehouseId());
    Optional<CustomerData> customerData =
        customerRepository.findById(newOrderRequest.getCustomerId());

    if (warehouseData.isEmpty() || customerData.isEmpty()) {
      throw new IllegalArgumentException();
    }

    Optional<DistrictData> districtData =
        districtRepository.findById(newOrderRequest.getDistrictId());
    if (districtData.isEmpty()) {
      throw new IllegalArgumentException();
    }

    List<StockUpdateDto> stockUpdates = new ArrayList<>();
    boolean allLocal = true;

    for (NewOrderRequestItem item : newOrderRequest.getItems()) {
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
            districtData.get().getId(),
            customerData.get().getId(),
            LocalDateTime.now(),
            stockUpdates.size(),
            allLocal);
    storedOrder = storeOrder(order, stockUpdates);
    return storedOrder;
  }

  private NewOrderResponse getNewOrderResponse(
      NewOrderRequest newOrderRequest, OrderData storedOrder) {
    if (storedOrder == null) {
      LOG.info("Cancel order processing");
      throw new AerospikeTransactionException("Order is not processable");
    }

    double orderItemSum = 0;
    // creation of return dtos
    List<NewOrderResponseItem> dtoItems = new ArrayList<>();

    List<OrderItemData> orderItems =
        orderItemRepository.getOrderItemsByOrder(storedOrder.getItemsIds());

    for (OrderItemData orderItem : orderItems) {
      Optional<ProductData> productData = productRepository.findById(orderItem.getProductRefId());
      dtoItems.add(
          new NewOrderResponseItem(
              orderItem.getSupplyingWarehouseRefId(),
              productData.get().getId(),
              productData.get().getName(),
              productData.get().getPrice(),
              orderItem.getAmount(),
              orderItem.getQuantity(),
              orderItem.getLeftQuantityInStock(),
              determineBrandGeneric(productData.get().getData(), "stock data")));
      orderItemSum += orderItem.getAmount();
    }

    Optional<DistrictData> districtData =
        districtRepository.findById(storedOrder.getDistrictRefId());
    Optional<WarehouseData> warehouse =
        warehouseRepository.findById(districtData.get().getWarehouseRefId());
    Optional<CustomerData> customerData =
        customerRepository.findById(storedOrder.getCustomerRefId());

    NewOrderResponse newOrderResponse =
        newOrderResponse(
            newOrderRequest,
            storedOrder.getId(),
            storedOrder.getEntryDate(),
            warehouse.get().getSalesTax(),
            districtData.get().getSalesTax(),
            customerData.get().getCredit(),
            customerData.get().getDiscount(),
            customerData.get().getLastName());
    newOrderResponse.setTotalAmount(
        calcOrderTotal(
            orderItemSum,
            customerData.get().getDiscount(),
            warehouse.get().getSalesTax(),
            districtData.get().getSalesTax()));

    newOrderResponse.setOrderItems(dtoItems);
    return newOrderResponse;
  }

  private OrderData storeOrder(OrderData order, List<StockUpdateDto> stockUpdates)
      throws AerospikeTransactionException {
    List<OrderItemData> itemDataList = updateStock(order, stockUpdates);
    if (itemDataList.isEmpty()) {
      throw new AerospikeTransactionException("Order item update failed");
    }

    for (OrderItemData orderItem : itemDataList) {
      order.getItemsIds().add(orderItem.getId());
    }

    // BATCH CALL
    orderItemRepository.saveOrderItemsInBatch(itemDataList);
    orderRepository.save(order);
    return order;
  }

  private List<OrderItemData> updateStock(OrderData order, List<StockUpdateDto> stockUpdates) {
    List<OrderItemData> orderItemsList = new ArrayList<>();
    int i = 0;
    for (i = 0; i < stockUpdates.size(); i++) {
      StockUpdateDto stockUpdateDto = stockUpdates.get(i);
      if (!stockUpdateDto.getStockData().reduceQuantity(stockUpdateDto.getQuantity())) {
        // TODO: A tmp-step with save, otherwise quantity update won't remain
        stockRepository.save(stockUpdateDto.getStockData());
        break;
      } else {

        Optional<ProductData> productData =
            productRepository.findById(stockUpdateDto.getStockData().getProductRefId());

        OrderItemData orderItem =
            new OrderItemData(
                order.getId(),
                stockUpdateDto.getStockData().getProductRefId(),
                stockUpdateDto.getStockData().getWarehouseRefId(),
                i,
                stockUpdateDto.getQuantity(),
                stockUpdateDto.getStockData().getQuantity(),
                stockUpdateDto.getQuantity() * productData.get().getPrice(),
                stockUpdateDto.getStockData().getDist01());

        // orderItemRepository.save(orderItem);
        orderItemsList.add(orderItem);
      }
    }

    // compensate the first transactions, if some updates fail
    if (i != stockUpdates.size()) {
      for (int j = 0; j < i; j++) {
        StockUpdateDto stockUpdateDto = stockUpdates.get(j);
        stockUpdateDto.getStockData().undoReduceQuantityOperation(stockUpdateDto.getQuantity());
      }
      return List.of();
    }

    return orderItemsList;
  }

  private StockData findByWarehouseIdAndProductId(String productId, String supplyingWarehouseId) {
    Optional<WarehouseData> warehouseData = warehouseRepository.findById(supplyingWarehouseId);
    Optional<ProductData> productData = productRepository.findById(productId);

    return stockRepository.getStocksByWarehouse(warehouseData.get().getStockRefsIds()).stream()
        .filter(stock -> stock.getProductRefId().equals(productData.get().getId()))
        .findFirst()
        .orElse(null);
  }
}

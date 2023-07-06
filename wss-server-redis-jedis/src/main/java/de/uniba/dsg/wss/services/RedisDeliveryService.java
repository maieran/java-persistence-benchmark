package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisDeliveryService extends DeliveryService {

  /* TODO: We need to check if deadlocks are happening and if the redis client is capable of
      handling himself or whether self-made concurrent handling is needed to be implemented
  */

  private static final Logger LOG = LogManager.getLogger(RedisDeliveryService.class);

  private final WarehouseRepository warehouseRepository;
  private final CarrierRepository carrierRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final DistrictRepository districtRepository;
  private final OrderItemRepository orderItemRepository;

  @Autowired
  public RedisDeliveryService(
      WarehouseRepository warehouseRepository,
      CarrierRepository carrierRepository,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      DistrictRepository districtRepository,
      OrderItemRepository orderItemRepository) {
    this.warehouseRepository = warehouseRepository;
    this.carrierRepository = carrierRepository;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.districtRepository = districtRepository;
    this.orderItemRepository = orderItemRepository;
  }

  @Override
  public DeliveryResponse process(DeliveryRequest deliveryRequest) {
    WarehouseData warehouse = warehouseRepository.findById(deliveryRequest.getWarehouseId());
    CarrierData carrierData = carrierRepository.findById(deliveryRequest.getCarrierId());

    // Find an order for each district (the oldest unfulfilled order)

    List<String> districtIds = warehouse.getDistrictRefsIds();
    List<OrderData> unfulfilledAndOldestOrders = new ArrayList<>();

    LocalDateTime oldestEntryDate = null;
    OrderData oldestUnfulfilledOrder = null;

    for (String id : districtIds) {
      DistrictData district = districtRepository.findById(id);

      for (String orderId : district.getOrderRefsIds()) {
        OrderData order = orderRepository.findById(orderId);

        if (!order.isFulfilled()) {
          if (oldestEntryDate == null || order.getEntryDate().isBefore(oldestEntryDate)) {
            oldestEntryDate = order.getEntryDate();
            oldestUnfulfilledOrder = order;
          }
        }
      }
      unfulfilledAndOldestOrders.add(oldestUnfulfilledOrder);
    }

    updateDeliveryStatusOfOldestUnfulfilledOrders(unfulfilledAndOldestOrders, carrierData);

    return new DeliveryResponse(deliveryRequest);
  }

  private void updateDeliveryStatusOfOldestUnfulfilledOrders(
      List<OrderData> unfulfilledAndOldestOrders, CarrierData carrier) {
    for (OrderData order : unfulfilledAndOldestOrders) {

      // TODO: Left for checking out how it will behave when concurrency comes
      if (order.isFulfilled()) {
        LOG.info("HOUSTON WE HAVE A PROBLEM!!!!");
        continue;
      }
      CustomerData customer = customerRepository.findById(order.getCustomerRefId());

      order.setCarrierRefId(carrier.getId());
      order.setFulfilled(true);

      double amount = 0;

      // TODO: REDIS BATCH CALL
      for (String orderItemId : order.getItemsIds()) {
        OrderItemData orderItem = orderItemRepository.findById(orderItemId);
        orderItem.updateDeliveryDate();
        amount += orderItem.getAmount();
        orderItemRepository.storeUpdatedOrderItem(orderItem);
      }
      customer.setBalance(amount);
      customer.increaseDeliveryCount();

      customerRepository.storeUpdatedCustomer(customer);
      orderRepository.storeUpdatedOrder(order);
    }
  }
}

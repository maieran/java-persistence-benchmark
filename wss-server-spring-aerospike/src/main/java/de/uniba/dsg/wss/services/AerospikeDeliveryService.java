package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link DeliveryService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 * @author Andre Maier
 */
@Service
public class AerospikeDeliveryService extends DeliveryService {

  private final WarehouseRepository warehouseRepository;
  private final CarrierRepository carrierRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final DistrictRepository districtRepository;
  private final OrderItemRepository orderItemRepository;

  @Autowired
  public AerospikeDeliveryService(
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
    Optional<WarehouseData> warehouse =
        warehouseRepository.findById(deliveryRequest.getWarehouseId());
    Optional<CarrierData> carrierData = carrierRepository.findById(deliveryRequest.getCarrierId());

    // Find an order for each district (the oldest unfulfilled order)
    List<DistrictData> districts =
        districtRepository.getDistrictsFromWarehouse(warehouse.get().getDistrictRefsIds());

    List<OrderData> unfulfilledAndOldestOrders =
        districts.stream()
            .map(
                district -> {
                  List<OrderData> orders =
                      orderRepository.getOrdersFromDistrict(district.getOrderRefsIds());

                  return orders.stream()
                      .filter(order -> !order.isFulfilled())
                      .min(Comparator.comparing(OrderData::getEntryDate))
                      .orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    updateDeliveryStatusOfOldestUnfulfilledOrders(unfulfilledAndOldestOrders, carrierData);

    return new DeliveryResponse(deliveryRequest);
  }

  private void updateDeliveryStatusOfOldestUnfulfilledOrders(
      List<OrderData> unfulfilledAndOldestOrders, Optional<CarrierData> carrier) {

    unfulfilledAndOldestOrders.stream()
        .filter(order -> !order.isFulfilled())
        .forEach(
            order -> {
              Optional<CustomerData> customer =
                  customerRepository.findById(order.getCustomerRefId());

              order.setCarrierRefId(carrier.get().getId());
              order.setFulfilled(true);

              double amount = 0;

              List<OrderItemData> orderItems =
                  orderItemRepository.getOrderItemsByOrder(order.getItemsIds());
              for (OrderItemData orderItem : orderItems) {
                orderItem.updateDeliveryDate();
                amount += orderItem.getAmount();
                orderItemRepository.storeUpdatedOrderItem(orderItem);
              }
              customer.get().setBalance(amount);
              customer.get().increaseDeliveryCount();

              customerRepository.storeUpdatedCustomer(customer);
              orderRepository.storeUpdatedOrder(order);
            });
  }
}

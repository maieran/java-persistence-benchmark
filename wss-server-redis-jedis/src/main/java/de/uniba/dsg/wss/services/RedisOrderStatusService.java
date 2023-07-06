package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.OrderItemStatusResponse;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.service.OrderStatusService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisOrderStatusService extends OrderStatusService {

  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CarrierRepository carrierRepository;

  @Autowired
  public RedisOrderStatusService(
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      CustomerRepository customerRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      CarrierRepository carrierRepository) {
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.carrierRepository = carrierRepository;
  }

  @Override
  public OrderStatusResponse process(OrderStatusRequest orderStatusRequest) {
    CustomerData customer;
    if (orderStatusRequest.getCustomerId() == null) {
      customer =
          customerRepository.getCustomers().entrySet().stream()
              .parallel()
              .filter(c -> c.getValue().getEmail().equals(orderStatusRequest.getCustomerEmail()))
              .findAny()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Failed to find customer with email "
                              + orderStatusRequest.getCustomerEmail()))
              .getValue();
    } else {
      customer = customerRepository.findById(orderStatusRequest.getCustomerId());
      if (customer == null) {
        throw new IllegalStateException(
            "Failed to find customer with email " + orderStatusRequest.getCustomerEmail());
      }
    }

    // TODO: BATCH CALL
    OrderData mostRecentOrder =
        customer.getOrderRefsIds().values().stream()
            .map(orderRepository::findById)
            .filter(Objects::nonNull)
            .max(Comparator.comparing(OrderData::getEntryDate))
            .orElseThrow(IllegalStateException::new);

    DistrictData district = districtRepository.findById(mostRecentOrder.getDistrictRefId());
    WarehouseData warehouse = warehouseRepository.findById(district.getWarehouseRefId());

    OrderStatusResponse orderStatusResponse =
        new OrderStatusResponse(
            warehouse.getId(),
            district.getId(),
            customer.getId(),
            customer.getFirstName(),
            customer.getMiddleName(),
            customer.getLastName(),
            customer.getBalance(),
            mostRecentOrder.getId(),
            mostRecentOrder.getEntryDate(),
            carrierRepository.findById(mostRecentOrder.getCarrierRefId()) == null
                ? null
                : mostRecentOrder.getCarrierRefId(),
            null // Set 'itemStatus' as null initially
            );

    List<OrderItemStatusResponse> itemStatusList =
        mostRecentOrder.getItemsIds().stream()
            .map(
                itemId -> {
                  // TODO: REDIS BATCH CALL ????
                  OrderItemData item = orderItemRepository.findById(itemId);
                  return new OrderItemStatusResponse(
                      item.getSupplyingWarehouseRefId(),
                      item.getProductRefId(),
                      item.getQuantity(),
                      item.getAmount(),
                      item.getDeliveryDate());
                })
            .collect(Collectors.toList());

    orderStatusResponse.setItemStatus(itemStatusList);

    return orderStatusResponse;
  }
}

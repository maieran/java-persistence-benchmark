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
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AerospikeOrderStatusService extends OrderStatusService {

  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CarrierRepository carrierRepository;

  @Autowired
  public AerospikeOrderStatusService(
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
    Optional<CustomerData> customer;
    if (orderStatusRequest.getCustomerId() == null) {
      customer =
          Optional.ofNullable(
              customerRepository.getCustomers().entrySet().stream()
                  .parallel()
                  .filter(
                      c -> c.getValue().getEmail().equals(orderStatusRequest.getCustomerEmail()))
                  .findAny()
                  .orElseThrow(
                      () ->
                          new IllegalStateException(
                              "Failed to find customer with email "
                                  + orderStatusRequest.getCustomerEmail()))
                  .getValue());
    } else {
      customer = customerRepository.findById(orderStatusRequest.getCustomerId());
      if (customer.isEmpty()) {
        throw new IllegalStateException(
            "Failed to find customer with email " + orderStatusRequest.getCustomerEmail());
      }
    }

    OrderData mostRecentOrder =
        orderRepository.getOrdersByCustomer(customer.get().getOrderRefsIds()).stream()
            .filter(Objects::nonNull)
            .max(Comparator.comparing(OrderData::getEntryDate))
            .orElseThrow(IllegalStateException::new);

    Optional<DistrictData> district =
        districtRepository.findById(mostRecentOrder.getDistrictRefId());
    Optional<WarehouseData> warehouse =
        warehouseRepository.findById(district.get().getWarehouseRefId());

    OrderStatusResponse orderStatusResponse =
        new OrderStatusResponse(
            warehouse.get().getId(),
            district.get().getId(),
            customer.get().getId(),
            customer.get().getFirstName(),
            customer.get().getMiddleName(),
            customer.get().getLastName(),
            customer.get().getBalance(),
            mostRecentOrder.getId(),
            mostRecentOrder.getEntryDate(),
            carrierRepository.findByCarrierId(mostRecentOrder.getCarrierRefId()) == null
                ? null
                : mostRecentOrder.getCarrierRefId(),
            null // Set 'itemStatus' as null, will be set down below
            );

    List<OrderItemStatusResponse> itemStatusList =
        orderItemRepository
            .getOrderItemsByIds(mostRecentOrder.getItemsIds())
            .entrySet()
            .parallelStream()
            .map(
                entry -> {
                  OrderItemData item = entry.getValue();
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

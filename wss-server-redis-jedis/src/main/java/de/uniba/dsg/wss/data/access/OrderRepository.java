package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.List;
import java.util.Map;

public interface OrderRepository {
  void saveAll(Map<String, OrderData> idsToOrders);

  OrderData findById(String orderId);

  void storeUpdatedOrder(OrderData order);

  void save(OrderData order);

  List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds);

  // List<OrderData> updateDeliveryStatusOfOldestUnfulfilledOrders(List<OrderData> orders,
  // CarrierData carrierData);
}

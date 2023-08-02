package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link OrderData orders}.
 *
 * @author Andre Maier
 */
public interface OrderRepository {
  void saveAll(Map<String, OrderData> idsToOrders);

  OrderData findById(String orderId);

  void storeUpdatedOrder(OrderData order);

  void save(OrderData order);

  List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds);

  Map<String, OrderData> getOrders();

  List<OrderData> getOrdersByCustomer(Map<String, String> orderRefsIds);
}

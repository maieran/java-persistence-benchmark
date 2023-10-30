package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.List;
import java.util.Map;

/**
 * This interface provides custom operations specific to OrderData in the Aerospike repository, that
 * are implemented in {@link OrderRepositoryOperationsImpl} and are extended by {@link
 * OrderRepository}.
 *
 * @author Andre Maier
 */
public interface OrderRepositoryOperations {
  void saveAll(Map<String, OrderData> idsToOrders);

  List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds);

  void storeUpdatedOrder(OrderData order);

  Map<String, OrderData> getOrders();

  List<OrderData> getOrdersByCustomer(Map<String, String> orderRefsIds);
}

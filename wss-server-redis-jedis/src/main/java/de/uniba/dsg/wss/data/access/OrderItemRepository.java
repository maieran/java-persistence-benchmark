package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.List;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link OrderItemData orderItems}.
 *
 * @author Andre Maier
 */
public interface OrderItemRepository {
  void saveAll(Map<String, OrderItemData> carriers);

  OrderItemData findById(String itemId);

  void storeUpdatedOrderItem(OrderItemData orderItem);

  void save(OrderItemData orderItem);

  List<OrderItemData> getOrderItemsByOrder(List<String> orderItemsIds);

  void saveOrderItemsInBatch(List<OrderItemData> orderItemsList);

  Map<String, OrderItemData> getOrderItemsByIds(List<String> itemIds);

  void deleteAll();
}

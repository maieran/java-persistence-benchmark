package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.List;
import java.util.Map;

/**
 * This interface provides custom operations specific to OrderItemData in the Aerospike repository,
 * that are implemented in {@link OrderItemRepositoryOperationsImpl} and are extended by {@link
 * OrderItemRepository}.
 *
 * @author Andre Maier
 */
public interface OrderItemRepositoryOperations {
  void saveAll(Map<String, OrderItemData> getIdsToOrderItems);

  List<OrderItemData> getOrderItemsByOrder(List<String> itemsIds);

  void storeUpdatedOrderItem(OrderItemData orderItem);

  void saveOrderItemsInBatch(List<OrderItemData> orderItemsList);

  Map<String, OrderItemData> getOrderItemsByIds(List<String> itemsIds);
}

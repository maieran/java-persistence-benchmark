package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.Map;

public interface OrderItemRepository {
  void saveAll(Map<String, OrderItemData> carriers);

  OrderItemData findById(String itemId);

  void storeUpdatedOrderItem(OrderItemData orderItem);
}

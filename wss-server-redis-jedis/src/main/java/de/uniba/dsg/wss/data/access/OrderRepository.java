package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.Map;

public interface OrderRepository {
  void saveAll(Map<String, OrderData> idsToOrders);

  OrderData findById(String orderId);
}

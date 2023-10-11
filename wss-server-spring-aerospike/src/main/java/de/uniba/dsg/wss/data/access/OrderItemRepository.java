package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface OrderItemRepository
    extends AerospikeRepository<OrderItemData, String>, OrderItemRepositoryOperations {
  void saveAll(Map<String, OrderItemData> getIdsToOrderItems);

  List<OrderItemData> getOrderItemsByOrder(List<String> itemsIds);
}

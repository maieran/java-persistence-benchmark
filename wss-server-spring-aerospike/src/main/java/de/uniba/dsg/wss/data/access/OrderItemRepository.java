package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface OrderItemRepository extends AerospikeRepository<OrderItemData, Integer> {
  <S extends OrderItemData> Iterable<S> saveAll(Map<String, OrderItemData> getIdsToOrderItems);
}

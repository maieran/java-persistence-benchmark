package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

/**
 * Aerospike repository for accessing and modifying {@link OrderItemData orderItems}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to orderItems by {@link OrderItemRepositoryOperations}.
 *
 * @author Andre Maier
 */
public interface OrderItemRepository
    extends AerospikeRepository<OrderItemData, String>, OrderItemRepositoryOperations {
  void saveAll(Map<String, OrderItemData> getIdsToOrderItems);

  List<OrderItemData> getOrderItemsByOrder(List<String> itemsIds);
}

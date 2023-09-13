package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.Map;

public interface OrderItemRepository extends AerospikeRepository<OrderItemData, Integer> {
    <S extends OrderItemData> Iterable<S> saveAll(Map<String, OrderItemData> getIdsToOrderItems);
}

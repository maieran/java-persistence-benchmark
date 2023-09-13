package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.OrderData;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(readOnly = true)
public interface OrderRepository extends AerospikeRepository<OrderData, Integer> {

    <S extends OrderData> Iterable<S> saveAll(Map<String, OrderData> idsToOrders);
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface OrderRepository extends AerospikeRepository<OrderData, Integer> {

  <S extends OrderData> Iterable<S> saveAll(Map<String, OrderData> idsToOrders);
}

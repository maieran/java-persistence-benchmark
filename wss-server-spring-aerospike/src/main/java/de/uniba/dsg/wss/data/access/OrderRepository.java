package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;

import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface OrderRepository extends AerospikeRepository<OrderData, String>, OrderRepositoryOperations {

  void saveAll(Map<String, OrderData> idsToOrders);

  List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds);
}

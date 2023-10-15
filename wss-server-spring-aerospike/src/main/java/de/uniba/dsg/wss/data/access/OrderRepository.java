package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aerospike repository for accessing and modifying {@link OrderData orders}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to orders by {@link OrderRepositoryOperations}.
 *
 * @author Andre Maier
 */
@Transactional(readOnly = true)
public interface OrderRepository
    extends AerospikeRepository<OrderData, String>, OrderRepositoryOperations {

  void saveAll(Map<String, OrderData> idsToOrders);

  List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds);
}

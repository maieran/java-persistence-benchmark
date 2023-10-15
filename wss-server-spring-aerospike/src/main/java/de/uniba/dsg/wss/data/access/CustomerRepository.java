package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aerospike repository for accessing and modifying {@link CustomerData customers}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to customers by {@link CustomerRepositoryOperations}.
 *
 * @author Andre Maier
 */
@Transactional(readOnly = true)
public interface CustomerRepository
    extends AerospikeRepository<CustomerData, String>, CustomerRepositoryOperations {
  void saveAll(Map<String, CustomerData> idsToCustomers);

  List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds);
}

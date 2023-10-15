package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This interface provides custom operations specific to CustomerData in the Aerospike repository,
 * that are implemented in {@link CustomerRepositoryOperationsImpl} and are extended by {@link
 * CustomerRepository}.
 *
 * @author Andre Maier
 */
public interface CustomerRepositoryOperations {
  void saveAll(Map<String, CustomerData> idsToCustomers);

  List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds);

  void storeUpdatedCustomer(Optional<CustomerData> customer);

  Map<String, CustomerData> getCustomers();
}

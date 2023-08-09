package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link CustomerData customers}.
 *
 * @author Andre Maier
 */
public interface CustomerRepository {
  void saveAll(Map<String, CustomerData> idsToCustomers);

  CustomerData findById(String customerId);

  void storeUpdatedCustomer(CustomerData customer);

  Map<String, CustomerData> getCustomers();

  void save(CustomerData copiedCustomer);

  List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds);
}

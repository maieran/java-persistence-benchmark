package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomerRepositoryOperations {
  void saveAll(Map<String, CustomerData> idsToCustomers);

  List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds);

  void storeUpdatedCustomer(Optional<CustomerData> customer);

  Map<String, CustomerData> getCustomers();
}

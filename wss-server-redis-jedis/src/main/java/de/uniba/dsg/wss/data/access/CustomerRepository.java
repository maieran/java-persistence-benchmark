package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.Map;

public interface CustomerRepository {
  void saveAll(Map<String, CustomerData> idsToCustomers);

  CustomerData findById(String customerId);

  void storeUpdatedCustomer(CustomerData customer);

  Map<String, CustomerData> getCustomers();

  void save(CustomerData copiedCustomer);
}

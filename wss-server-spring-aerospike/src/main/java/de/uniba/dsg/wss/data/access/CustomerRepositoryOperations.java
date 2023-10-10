package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;

public interface CustomerRepositoryOperations {
  void saveAll(Map<String, CustomerData> idsToCustomers);

  List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds);
}

package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class CustomerRepositoryOperationsImpl implements CustomerRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public CustomerRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, CustomerData> idsToCustomers) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToCustomers.forEach((id, customer) -> aerospikeTemplate.save(customer));
  }

  // TODO: HOW TO BATCH READ -  getCustomersByDistricts ?!
  @Override
  public List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds) {
    List<CustomerData> customers = new ArrayList<>();

    for (String id : customerRefsIds) {
      // Read the record for the key
      CustomerData customer = aerospikeTemplate.findById(id, CustomerData.class);

      // Check if the record exists
      if (customer != null) {
        customers.add(customer);
      }
    }

    return customers;
  }

  @Override
  public void storeUpdatedCustomer(Optional<CustomerData> customer) {
    aerospikeTemplate.update(customer.get());
  }

  @Override
  public Map<String, CustomerData> getCustomers() {
    return aerospikeTemplate
        .findAll(CustomerData.class)
        .collect(Collectors.toMap(CustomerData::getId, customer -> customer));
  }
}

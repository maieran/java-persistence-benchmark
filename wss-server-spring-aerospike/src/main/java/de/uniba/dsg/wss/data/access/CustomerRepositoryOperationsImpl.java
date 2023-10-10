package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;
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

  @Override
  public List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds) {
    return null;
  }
}

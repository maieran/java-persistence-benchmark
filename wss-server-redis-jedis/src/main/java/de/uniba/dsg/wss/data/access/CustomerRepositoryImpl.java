package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.uniba.dsg.wss.data.model.WarehouseData;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link CustomerData customers}.
 *
 * @author Andre Maier
 */
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

  private final HashOperations<String, String, CustomerData> hashOperations;

  public CustomerRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, CustomerData> customers) {
    String hashKey = "customers";
    hashOperations.putAll(hashKey, customers);
  }

  @Override
  public CustomerData findById(String customerId) {
    String hashKey = "customers";
    return hashOperations.get(hashKey, customerId);
  }

  @Override
  public void storeUpdatedCustomer(CustomerData customer) {
    String hashKey = "customers";
    hashOperations.put(hashKey, customer.getId(), customer);
  }

  @Override
  public Map<String, CustomerData> getCustomers() {
    String hashKey = "customers";
    return hashOperations.entries(hashKey);
  }

  @Override
  public void save(CustomerData copiedCustomer) {
    String hashKey = "customers";
    hashOperations.put(hashKey, copiedCustomer.getId(), copiedCustomer);
  }

  @Override
  public List<CustomerData> getCustomersByDistricts(List<String> customerRefsIds) {
    String hashKey = "customers";
    List<CustomerData> customers = hashOperations.multiGet(hashKey, customerRefsIds);

    return customers.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }
}

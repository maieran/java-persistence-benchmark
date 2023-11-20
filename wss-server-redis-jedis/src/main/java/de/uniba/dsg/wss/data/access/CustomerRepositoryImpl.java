package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link CustomerData
 * customers}.
 *
 * @author Andre Maier
 */
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, CustomerData> hashOperations;

  public CustomerRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, CustomerData> customers) {
    String hashKey = "customers";
    hashOperations.putAll(hashKey, customers);
  }*/

  @Override
  public void saveAll(Map<String, CustomerData> customers) {
    String hashKey = "customers";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < customers.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, customers.size());
      Map<String, CustomerData> batch = getBatch(customers, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, CustomerData> getBatch(
      Map<String, CustomerData> customers, int start, int end) {
    return customers.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

  @Override
  public void deleteAll() {
    String hashKey = "customers";
    redisTemplate.delete(hashKey);
  }
}

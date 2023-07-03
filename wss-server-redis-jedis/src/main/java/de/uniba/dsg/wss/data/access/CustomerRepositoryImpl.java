package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

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
}

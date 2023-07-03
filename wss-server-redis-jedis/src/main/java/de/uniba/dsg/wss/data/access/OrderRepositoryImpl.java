package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

  private final HashOperations<String, String, OrderData> hashOperations;

  public OrderRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, OrderData> orders) {
    String hashKey = "orders";
    hashOperations.putAll(hashKey, orders);
  }

  @Override
  public OrderData findById(String orderId) {
    String hashKey = "orders";
    return hashOperations.get(hashKey, orderId);
  }
}

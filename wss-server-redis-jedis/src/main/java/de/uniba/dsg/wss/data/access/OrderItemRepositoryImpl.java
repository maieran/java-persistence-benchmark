package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

  private final HashOperations<String, String, OrderItemData> hashOperations;

  public OrderItemRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, OrderItemData> orderItems) {
    String hashKey = "orderItems";
    hashOperations.putAll(hashKey, orderItems);
  }

  @Override
  public OrderItemData findById(String itemId) {
    String hashKey = "orderItems";
    return hashOperations.get(hashKey, itemId);
  }

  @Override
  public void storeUpdatedOrderItem(OrderItemData orderItem) {
    String hashKey = "orderItems";
    hashOperations.put(hashKey, orderItem.getId(), orderItem);
  }
}

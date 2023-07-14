package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, OrderData> hashOperations;
  private static final Logger LOG = LogManager.getLogger(OrderRepository.class);

  public OrderRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
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

  @Override
  public void storeUpdatedOrder(OrderData order) {
    String hashKey = "orders";
    hashOperations.put(hashKey, order.getId(), order);
  }

  @Override
  public void save(OrderData order) {
    String hashKey = "orders";
    hashOperations.put(hashKey, order.getId(), order);
  }

  @Override
  public List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds) {
    String hashKey = "orders";
    List<OrderData> orderDataList = new ArrayList<>();

    for (String orderRefId : orderRefsIds) {
      OrderData orderData = hashOperations.get(hashKey, orderRefId);
      if (orderData != null) {
        orderDataList.add(orderData);
      }
    }

    return orderDataList;
  }

  @Override
  public Map<String, OrderData> getOrders() {
    String hashKey = "orders";
    return hashOperations.entries(hashKey);
  }

  // https://docs.spring.io/spring-data-redis/docs/current/reference/html/#pipeline

  /**
   * @Override public List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds) { String
   * script = "local results = {} " + "for _, orderRefId in ipairs(ARGV) do " + " local orderData =
   * redis.call('HGET', KEYS[1], orderRefId) " + " table.insert(results, orderData) " + "end " +
   * "return results ";
   *
   * <p>RedisScript<List<OrderData>> redisScript = new DefaultRedisScript<>(script, List.class);
   * List<OrderData> orderDataList = redisTemplate.execute(redisScript,
   * Collections.singletonList("orders"), orderRefsIds);
   *
   * <p>orderDataList.removeIf(Objects::isNull);
   *
   * <p>return orderDataList; }
   */
}

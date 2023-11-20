package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link OrderData orders}.
 *
 * @author Andre Maier
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, OrderData> hashOperations;
  private static final Logger LOG = LogManager.getLogger(OrderRepository.class);

  public OrderRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, OrderData> orders) {
    String hashKey = "orders";
    hashOperations.putAll(hashKey, orders);
  }*/

  @Override
  public void saveAll(Map<String, OrderData> orders) {
    String hashKey = "orders";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < orders.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, orders.size());
      Map<String, OrderData> batch = getBatch(orders, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, OrderData> getBatch(Map<String, OrderData> orders, int start, int end) {
    return orders.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
    List<OrderData> orders = hashOperations.multiGet(hashKey, orderRefsIds);

    return orders.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public Map<String, OrderData> getOrders() {
    String hashKey = "orders";
    return hashOperations.entries(hashKey);
  }

  @Override
  public List<OrderData> getOrdersByCustomer(Map<String, String> orderRefsIds) {
    String hashKey = "orders";
    Map<String, OrderData> allOrders = hashOperations.entries(hashKey);

    return allOrders.entrySet().stream()
        .filter(entry -> orderRefsIds.containsKey(entry.getKey()))
        .map(Map.Entry::getValue)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteAll() {
    String hashKey = "orders";
    redisTemplate.delete(hashKey);
  }
}

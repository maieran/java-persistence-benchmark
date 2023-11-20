package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link OrderItemData
 * orderItems}.
 *
 * @author Andre Maier
 */
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, OrderItemData> hashOperations;

  public OrderItemRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, OrderItemData> orderItems) {
    String hashKey = "orderItems";
    hashOperations.putAll(hashKey, orderItems);
  }*/

  @Override
  public void saveAll(Map<String, OrderItemData> orderItems) {
    String hashKey = "orderItems";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < orderItems.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, orderItems.size());
      Map<String, OrderItemData> batch = getBatch(orderItems, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, OrderItemData> getBatch(
      Map<String, OrderItemData> orderItems, int start, int end) {
    return orderItems.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

  @Override
  public void save(OrderItemData orderItem) {
    String hashKey = "orderItems";
    hashOperations.put(hashKey, orderItem.getId(), orderItem);
  }

  @Override
  public List<OrderItemData> getOrderItemsByOrder(List<String> orderItemsIds) {
    String hashKey = "orderItems";
    List<OrderItemData> orderItems = hashOperations.multiGet(hashKey, orderItemsIds);

    return orderItems.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public void saveOrderItemsInBatch(List<OrderItemData> orderItemsList) {
    String hashKey = "orderItems";
    Map<String, OrderItemData> orderItems = new HashMap<>();
    for (OrderItemData orderItem : orderItemsList) {
      orderItems.put(orderItem.getId(), orderItem);
    }

    hashOperations.putAll(hashKey, orderItems);
  }

  @Override
  public Map<String, OrderItemData> getOrderItemsByIds(List<String> orderItemIds) {
    String hashKey = "orderItems";
    Map<String, OrderItemData> orderItems = new HashMap<>();
    List<OrderItemData> orderItemList = hashOperations.multiGet(hashKey, orderItemIds);

    for (OrderItemData orderItem : orderItemList) {
      if (orderItem != null) {
        orderItems.put(orderItem.getId(), orderItem);
      }
    }

    return orderItems;
  }

  @Override
  public void deleteAll() {
    String hashKey = "orderItems";
    redisTemplate.delete(hashKey);
  }
}

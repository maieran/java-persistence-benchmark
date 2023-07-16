package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.*;
import java.util.stream.Collectors;
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
}

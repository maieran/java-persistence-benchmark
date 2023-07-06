package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

  private final HashOperations<String, String, OrderData> hashOperations;
  private static final Logger LOG = LogManager.getLogger(OrderRepository.class);

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

  //  @Override
  //  public List<OrderData> updateDeliveryStatusOfOldestUnfulfilledOrders(List<OrderData> orders,
  // CarrierData carrierData) {
  //    String hashKey = "orders";
  //    List<OrderData> oldestOrders = new ArrayList<>();
  //
  //    for(OrderData order : orders){
  //      // TODO: Need to see if we need to handle concurrency here
  //      if (order.isFulfilled()) {
  //        LOG.info("HOUSTON WE HAVE A PROBLEM!!!!");
  //        continue;
  //      }
  //
  //      String customerRefId = order.getCustomerRefId();
  //      CustomerData customerData = customerRepository.findById(customerRefId);
  //
  //
  //
  //    }
  //
  //
  //
  //
  //
  //    return null;
  //  }
}

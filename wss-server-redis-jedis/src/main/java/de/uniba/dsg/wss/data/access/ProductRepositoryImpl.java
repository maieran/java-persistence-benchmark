package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, ProductData> hashOperations;

  public ProductRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, ProductData> products) {
    String hashKey = "products";
    hashOperations.putAll(hashKey, products);
  }

  @Override
  public Map<String, ProductData> getAllProducts() {
    String hashKey = "products";
    return hashOperations.entries(hashKey);
  }

  @Override
  public ProductData findById(String productRefId) {
    String hashKey = "products";
    return hashOperations.get(hashKey, productRefId);
  }
}

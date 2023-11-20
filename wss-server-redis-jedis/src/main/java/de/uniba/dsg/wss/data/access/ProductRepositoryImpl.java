package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link ProductData
 * products}.
 *
 * @author Andre Maier
 */
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

  @Override
  public Map<String, ProductData> getProductsFromStocks(List<String> productIds) {
    String hashKey = "products";
    List<ProductData> products = hashOperations.multiGet(hashKey, productIds);

    Map<String, ProductData> productDataMap = new HashMap<>();
    for (ProductData product : products) {
      if (product != null) {
        productDataMap.put(product.getId(), product);
      }
    }

    return productDataMap;
  }

  @Override
  public void deleteAll() {
    String hashKey = "products";
    redisTemplate.delete(hashKey);
  }
}

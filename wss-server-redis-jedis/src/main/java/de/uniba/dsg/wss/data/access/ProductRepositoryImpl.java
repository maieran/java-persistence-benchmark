package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
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
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, ProductData> hashOperations;

  public ProductRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*
    @Override
    public void saveAll(Map<String, ProductData> products) {
      String hashKey = "products";
      hashOperations.putAll(hashKey, products);
    }
  */

  @Override
  public void saveAll(Map<String, ProductData> products) {
    String hashKey = "products";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < products.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, products.size());
      Map<String, ProductData> batch = getBatch(products, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, ProductData> getBatch(Map<String, ProductData> products, int start, int end) {
    return products.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

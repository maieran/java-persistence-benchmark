package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link WarehouseData
 * warehouses}.
 *
 * @author Andre Maier
 */
@Repository
public class WarehouseRepositoryImpl implements WarehouseRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, WarehouseData> hashOperations;

  public WarehouseRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*
    @Override
    public void saveAll(Map<String, WarehouseData> warehouses) {
      String hashKey = "warehouses";
      hashOperations.putAll(hashKey, warehouses);
    }
  */

  @Override
  public void saveAll(Map<String, WarehouseData> warehouses) {
    String hashKey = "warehouses";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < warehouses.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, warehouses.size());
      Map<String, WarehouseData> batch = getBatch(warehouses, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, WarehouseData> getBatch(
      Map<String, WarehouseData> warehouses, int start, int end) {
    return warehouses.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public Map<String, WarehouseData> getWarehouses() {
    String hashKey = "warehouses";
    return hashOperations.entries(hashKey);
  }

  @Override
  public WarehouseData findById(String id) {
    String hashKey = "warehouses";
    return hashOperations.get(hashKey, id);
  }

  @Override
  public void save(WarehouseData warehouse) {
    String hashKey = "warehouses";
    hashOperations.put(hashKey, warehouse.getId(), warehouse);
  }

  @Override
  public void deleteAll() {
    String hashKey = "warehouses";
    redisTemplate.delete(hashKey);
  }
}

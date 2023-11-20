package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;
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
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, WarehouseData> hashOperations;

  public WarehouseRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, WarehouseData> warehouses) {
    String hashKey = "warehouses";
    hashOperations.putAll(hashKey, warehouses);
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

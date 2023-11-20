package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link CarrierData
 * carriers}.
 *
 * @author Andre Maier
 */
@Repository
public class CarrierRepositoryImpl implements CarrierRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, CarrierData> hashOperations;

  public CarrierRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, CarrierData> carriers) {
    String hashKey = "carriers";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < carriers.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, carriers.size());
      Map<String, CarrierData> batch = getBatch(carriers, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, CarrierData> getBatch(Map<String, CarrierData> carriers, int start, int end) {
    return carriers.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public Map<String, CarrierData> getCarriers() {
    String hashKey = "carriers";
    return hashOperations.entries(hashKey);
  }

  @Override
  public CarrierData findById(String carrierRefId) {
    if (carrierRefId != null) {
      String hashKey = "carriers";
      return hashOperations.get(hashKey, carrierRefId);
    }
    return null;
  }

  @Override
  public void deleteAll() {
    String hashKey = "carriers";
    redisTemplate.delete(hashKey);
  }
}

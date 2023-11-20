package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;
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

  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, CarrierData> hashOperations;

  public CarrierRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, CarrierData> carriers) {
    String hashKey = "carriers";
    hashOperations.putAll(hashKey, carriers);
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

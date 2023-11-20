package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link DistrictData
 * districts}.
 *
 * @author Andre Maier
 */
@Repository
public class DistrictRepositoryImpl implements DistrictRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, DistrictData> hashOperations;

  public DistrictRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, DistrictData> districts) {
    String hashKey = "districts";
    hashOperations.putAll(hashKey, districts);
  }*/

  @Override
  public void saveAll(Map<String, DistrictData> districts) {
    String hashKey = "districts";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < districts.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, districts.size());
      Map<String, DistrictData> batch = getBatch(districts, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, DistrictData> getBatch(
      Map<String, DistrictData> districts, int start, int end) {
    return districts.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public DistrictData findById(String districtId) {
    String hashKey = "districts";
    return hashOperations.get(hashKey, districtId);
  }

  @Override
  public void save(DistrictData district) {
    String hashKey = "districts";
    hashOperations.put(hashKey, district.getId(), district);
  }

  @Override
  public Map<String, DistrictData> getDistricts() {
    String hashKey = "districts";
    return hashOperations.entries(hashKey);
  }

  @Override
  public List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds) {
    String hashKey = "districts";
    List<DistrictData> districts = hashOperations.multiGet(hashKey, districtRefsIds);

    return districts.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public void deleteAll() {
    String hashKey = "districts";
    redisTemplate.delete(hashKey);
  }
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.uniba.dsg.wss.data.model.WarehouseData;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link DistrictData districts}.
 *
 * @author Andre Maier
 */
@Repository
public class DistrictRepositoryImpl implements DistrictRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, DistrictData> hashOperations;

  public DistrictRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, DistrictData> districts) {
    String hashKey = "districts";
    hashOperations.putAll(hashKey, districts);
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
}

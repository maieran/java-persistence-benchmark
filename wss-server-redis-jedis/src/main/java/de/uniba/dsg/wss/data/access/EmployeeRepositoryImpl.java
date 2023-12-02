package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link EmployeeData
 * employees}.
 *
 * @author Andre Maier
 */
@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, EmployeeData> hashOperations;

  public EmployeeRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, EmployeeData> idsToEmployees) {
    String hashKey = "employees";
    hashOperations.putAll(hashKey, idsToEmployees);
  }*/

  @Override
  public void saveAll(Map<String, EmployeeData> idsToEmployees) {
    String hashKey = "employees";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < idsToEmployees.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, idsToEmployees.size());
      Map<String, EmployeeData> batch = getBatch(idsToEmployees, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, EmployeeData> getBatch(
      Map<String, EmployeeData> idsToEmployees, int start, int end) {
    return idsToEmployees.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public EmployeeData findEmployeeByName(String username) {
    String hashKey = "employees";
    return hashOperations.entries(hashKey).get(username);
  }

  @Override
  public void deleteAll() {
    String hashKey = "employees";
    redisTemplate.delete(hashKey);
  }
}

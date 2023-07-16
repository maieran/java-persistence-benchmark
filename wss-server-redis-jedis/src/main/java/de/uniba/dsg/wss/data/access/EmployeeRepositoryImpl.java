package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {
  private final HashOperations<String, String, EmployeeData> hashOperations;

  public EmployeeRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, EmployeeData> idsToEmployees) {
    String hashKey = "employees";
    hashOperations.putAll(hashKey, idsToEmployees);
  }

  @Override
  public EmployeeData findEmployeeByName(String username) {
    String hashKey = "employees";
    return hashOperations.entries(hashKey).get(username);
  }
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StockRepositoryImpl implements StockRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, StockData> hashOperations;

  public StockRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, StockData> stocks) {
    String hashKey = "stocks";
    hashOperations.putAll(hashKey, stocks);
  }

  @Override
  public StockData findById(String stockId) {
    String hashKey = "stocks";
    return hashOperations.get(hashKey, stockId);
  }
}

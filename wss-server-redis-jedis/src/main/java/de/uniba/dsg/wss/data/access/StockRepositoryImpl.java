package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link StockData stocks}.
 *
 * @author Andre Maier
 */
@Repository
public class StockRepositoryImpl implements StockRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, StockData> hashOperations;

  public StockRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, StockData> stocks) {
    String hashKey = "stocks";
    hashOperations.putAll(hashKey, stocks);
  }*/

  @Override
  public void saveAll(Map<String, StockData> stocks) {
    String hashKey = "stocks";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < stocks.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, stocks.size());
      Map<String, StockData> batch = getBatch(stocks, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, StockData> getBatch(Map<String, StockData> stocks, int start, int end) {
    return stocks.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public StockData findById(String stockId) {
    String hashKey = "stocks";
    return hashOperations.get(hashKey, stockId);
  }

  @Override
  public Map<String, StockData> getStocks() {
    String hashKey = "stocks";
    return hashOperations.entries(hashKey);
  }

  @Override
  public List<StockData> getStocksByWarehouse(List<String> stockRefsIds) {
    String hashKey = "stocks";
    List<StockData> stocks = hashOperations.multiGet(hashKey, stockRefsIds);

    return stocks.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public void save(StockData stockData) {
    String hashKey = "stocks";
    hashOperations.put(hashKey, stockData.getId(), stockData);
  }

  @Override
  public void deleteAll() {
    String hashKey = "stocks";
    redisTemplate.delete(hashKey);
  }
}

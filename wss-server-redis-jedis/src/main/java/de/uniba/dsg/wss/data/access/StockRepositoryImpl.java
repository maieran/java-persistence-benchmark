package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.HashMap;
import java.util.List;
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

  @Override
  public Map<String, StockData> getStocks() {
    String hashKey = "stocks";
    return hashOperations.entries(hashKey);
  }

  @Override
  public Map<String, StockData> getStocksByWarehouse(List<String> stockRefsIds) {
    String hashKey = "stocks";
    List<StockData> stockDataList = hashOperations.multiGet(hashKey, stockRefsIds);

    Map<String, StockData> stocksByWarehouse = new HashMap<>();
    for (StockData stockData : stockDataList) {
      if (stockData != null) {
        stocksByWarehouse.put(stockData.getWarehouseRefId(), stockData);
      }
    }

    return stocksByWarehouse;
  }
}

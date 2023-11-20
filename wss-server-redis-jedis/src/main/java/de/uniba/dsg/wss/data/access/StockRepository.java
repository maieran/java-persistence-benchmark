package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link StockData stocks}.
 *
 * @author Andre Maier
 */
public interface StockRepository {
  void saveAll(Map<String, StockData> idsToStocks);

  StockData findById(String stockId);

  Map<String, StockData> getStocks();

  List<StockData> getStocksByWarehouse(List<String> stockRefsIds);

  void save(StockData stockData);

  void deleteAll();
}

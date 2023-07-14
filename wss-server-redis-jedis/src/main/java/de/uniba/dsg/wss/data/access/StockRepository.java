package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;

public interface StockRepository {
  void saveAll(Map<String, StockData> idsToStocks);

  StockData findById(String stockId);

  Map<String, StockData> getStocks();

  Map<String, StockData> getStocksByWarehouse(List<String> stockRefsIds);
}

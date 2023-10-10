package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;

public interface StockRepositoryOperations {
  void saveAll(Map<String, StockData> idsToStocks);

  List<StockData> getStocksByWarehouse(List<String> stockRefsIds);
}

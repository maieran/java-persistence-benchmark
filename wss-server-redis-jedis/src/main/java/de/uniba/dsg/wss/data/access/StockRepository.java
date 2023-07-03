package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.Map;

public interface StockRepository {
  void saveAll(Map<String, StockData> idsToStocks);

  StockData findById(String stockId);
}

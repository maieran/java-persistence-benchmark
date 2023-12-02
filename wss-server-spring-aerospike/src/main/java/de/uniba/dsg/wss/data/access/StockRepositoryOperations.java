package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;

/**
 * This interface provides custom operations specific to StockData in the Aerospike repository, that
 * are implemented in {@link StockRepositoryOperationsImpl} and are extended by {@link
 * StockRepository}.
 *
 * @author Andre Maier
 */
public interface StockRepositoryOperations {
  void saveAll(Map<String, StockData> idsToStocks);

  List<StockData> getStocksByWarehouse(List<String> stockRefsIds);

  Map<String, StockData> getStocks();
}

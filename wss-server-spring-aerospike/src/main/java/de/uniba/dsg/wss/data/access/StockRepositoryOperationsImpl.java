package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.StockData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class StockRepositoryOperationsImpl implements StockRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public StockRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, StockData> idsToStocks) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToStocks.forEach((id, stock) -> aerospikeTemplate.save(stock));
  }

  // TODO: HOW TO BATCH READ -  getStocksByWarehouse ?!
  @Override
  public List<StockData> getStocksByWarehouse(List<String> stockRefsIds) {
    List<StockData> stocks = new ArrayList<>();

    // Iterate over the district reference IDs and read each record individually
    for (String id : stockRefsIds) {
      // Read the record for the key
      StockData stockData = aerospikeTemplate.findById(id, StockData.class);

      // Check if the record exists
      if (stockData != null) {
        stocks.add(stockData);
      }
    }

    return stocks;
  }

  @Override
  public Map<String, StockData> getStocks() {
    return aerospikeTemplate
        .findAll(StockData.class)
        .collect(Collectors.toMap(StockData::getId, stock -> stock));
  }
}

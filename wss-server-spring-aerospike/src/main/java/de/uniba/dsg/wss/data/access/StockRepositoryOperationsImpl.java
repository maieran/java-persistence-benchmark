package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;
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

  @Override
  public List<StockData> getStocksByWarehouse(List<String> stockRefsIds) {
    return null;
  }
}

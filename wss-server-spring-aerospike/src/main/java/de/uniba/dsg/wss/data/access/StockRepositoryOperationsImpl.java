package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.StockData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link StockRepositoryOperations} interface for
 * accessing and modifying {@link StockData stocks}.
 *
 * @author Andre Maier
 */
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
    List<StockData> stocks = new ArrayList<>();
    // 1.Step - Collect the keys/ids necessary to retrieve the objects
    Key[] keys = new Key[stockRefsIds.size()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(StockData.class),
              stockRefsIds.get(i));
    }

    // 2.Step - Retrieve stockData from Aerospike data model
    Record[] records = aerospikeTemplate.getAerospikeClient().get(null, keys);

    // 3.Step - Populate the list of stocks
    for (int i = 0; i < records.length; i++) {
      Record record = records[i];
      if (record != null) {

        // Create the StockData instance
        StockData stock =
            new StockData(
                stockRefsIds.get(i), // Set the id using the stockRefsIds list
                record.getString("warehouseRefId"),
                record.getString("productRefId"),
                record.getInt("quantity"),
                record.getDouble("ytdBalance"),
                record.getInt("orderCount"),
                record.getInt("remoteCount"),
                record.getString("data"),
                record.getString("dist01"),
                record.getString("dist02"),
                record.getString("dist03"),
                record.getString("dist04"),
                record.getString("dist05"),
                record.getString("dist06"),
                record.getString("dist07"),
                record.getString("dist08"),
                record.getString("dist09"),
                record.getString("dist10"));
        stocks.add(stock);
      }
    }
    return stocks;
  }

  /*
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
  }*/

  @Override
  public Map<String, StockData> getStocks() {
    return aerospikeTemplate
        .findAll(StockData.class)
        .collect(Collectors.toMap(StockData::getId, stock -> stock));
  }
}

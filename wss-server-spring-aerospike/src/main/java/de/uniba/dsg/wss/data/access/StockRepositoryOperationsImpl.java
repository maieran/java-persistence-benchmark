package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.StockData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

  // TODO: BATCH_SIZE Implementation
  @Override
  public List<StockData> getStocksByWarehouse(List<String> stockRefsIds) {
    BatchPolicy batchPolicy = new BatchPolicy();
    int timeout_socket = 1800000;
    int timeout_read = 1800000;
    batchPolicy.setTimeouts(timeout_socket, timeout_read);

    Key[] keys =
        stockRefsIds.stream()
            .map(
                id ->
                    new Key(
                        aerospikeTemplate.getNamespace(),
                        aerospikeTemplate.getSetName(StockData.class),
                        id))
            .toArray(Key[]::new);

    Record[] records = aerospikeTemplate.getAerospikeClient().get(batchPolicy, keys);

    return IntStream.range(0, records.length)
        .parallel()
        .filter(i -> records[i] != null)
        .mapToObj(
            i -> {
              Record record = records[i];
              return new StockData(
                  stockRefsIds.get(i),
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
            })
        .collect(Collectors.toList());
  }

  // return stocks.stream().filter(Objects::nonNull).collect(Collectors.toList());

  @Override
  public Map<String, StockData> getStocks() {
    return aerospikeTemplate
        .findAll(StockData.class)
        .collect(Collectors.toMap(StockData::getId, stock -> stock));
  }
}

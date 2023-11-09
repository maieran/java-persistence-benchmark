package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.cdt.*;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.AddressData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link WarehouseRepositoryOperations} interface
 * for accessing and modifying {@link WarehouseData warehouses}.
 *
 * @author Andre Maier
 */
public class WarehouseRepositoryOperationsImpl implements WarehouseRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public WarehouseRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, WarehouseData> idsToWarehouse) {

    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    for (Map.Entry<String, WarehouseData> entry : idsToWarehouse.entrySet()) {
      Key key =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(WarehouseData.class),
              entry.getKey());

      Bin[] warehouseBins =
          new Bin[] {
            new Bin("id", entry.getKey()),
            new Bin("name", entry.getValue().getName()),
            new Bin(
                "address",
                new AddressData(
                    entry.getValue().getAddress().getStreet1(),
                    entry.getValue().getAddress().getStreet2(),
                    entry.getValue().getAddress().getZipCode(),
                    entry.getValue().getAddress().getCity(),
                    entry.getValue().getAddress().getState())),
            new Bin("salesTax", entry.getValue().getSalesTax()),
            new Bin("ytdBalance", entry.getValue().getYearToDateBalance()),
            // new Bin("stockRefsIds", entry.getValue().getStockRefsIds()),
            // new Bin("stockRefsIds", entry.getValue().getStockRefsIds().get(0)),
            new Bin("districtRefsIds", entry.getValue().getDistrictRefsIds())
          };

      aerospikeTemplate.getAerospikeClient().put(writePolicy, key, warehouseBins);

      /*
      TODO:
        Somehow need to solve the problem with long time initialization,
        right now it takes 22 min to initialize the data set. However, otherwise:
        Caused by: com.aerospike.client.AerospikeException: Error 13,1,0,30000,1000,0,BB9020011AC4202 127.0.0.1 3000: Record too big
        Supposedly there is an asynchronous variant, which may be faster.
        https://aerospike.github.io/spring-data-aerospike/  unsere Spring Boot Version gehört zu 2.7.xx
      */

      /*      List<String> stockRefsIds = entry.getValue().getStockRefsIds();
      int chunkSize = 100; // Setting the chunk size ~ 29,132 strings can be stored into 1Megabyte
      int chunks = (int) Math.ceil((double) stockRefsIds.size() / chunkSize);

      for (int i = 0; i < chunks; i++) {
        int fromIndex = i * chunkSize;
        int toIndex = Math.min(fromIndex + chunkSize, stockRefsIds.size());
        List<String> chunk = stockRefsIds.subList(fromIndex, toIndex);

        Operation listOperation =
            ListOperation.append(
                ListPolicy.Default, "stockRefsIds", Value.get(chunk.toArray(new String[0])));
        aerospikeTemplate.getAerospikeClient().operate(writePolicy, key, listOperation);*/

      List<String> stockRefsIds = entry.getValue().getStockRefsIds();
      int chunkSize = 20000;
      // 29000; // there are 100000 stocksRefsIds per warehouse, in total we are having 500000
      for (int i = 0; i < stockRefsIds.size(); i += chunkSize) {
        List<String> chunk = stockRefsIds.subList(i, Math.min(stockRefsIds.size(), i + chunkSize));
        Bin bin = new Bin("stockRefsIds", chunk);
        aerospikeTemplate.getAerospikeClient().put(writePolicy, key, bin);
      }
    }
  }

  /*
  private List<List<String>> chunkList(List<String> list, int chunkSize) {
    List<List<String>> chunks = new ArrayList<>();
    for (int i = 0; i < list.size(); i += chunkSize) {
      chunks.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
    }
    return chunks;
  }
   */

  @Override
  public Map<String, WarehouseData> getWarehouses() {
    return aerospikeTemplate
        .findAll(WarehouseData.class)
        .collect(Collectors.toMap(WarehouseData::getId, warehouse -> warehouse));
  }
}

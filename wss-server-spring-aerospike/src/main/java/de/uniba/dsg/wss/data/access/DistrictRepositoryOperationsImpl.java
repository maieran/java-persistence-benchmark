package de.uniba.dsg.wss.data.access;

import com.aerospike.client.*;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class DistrictRepositoryOperationsImpl implements DistrictRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;
  private final AerospikeClient aerospikeClient;

  @Autowired
  public DistrictRepositoryOperationsImpl(
      AerospikeTemplate aerospikeTemplate, AerospikeClient aerospikeClient) {
    this.aerospikeTemplate = aerospikeTemplate;
    this.aerospikeClient = aerospikeClient;
  }

  // TODO: HOW TO BATCH READ - getDistrictsFromWarehouse ?!
  @Override
  public List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds) {
    List<DistrictData> districts = new ArrayList<>();

    // Iterate over the district reference IDs and read each record individually
    for (String id : districtRefsIds) {
      // Read the record for the key
      DistrictData districtData = aerospikeTemplate.findById(id, DistrictData.class);

      // Check if the record exists
      if (districtData != null) {
        districts.add(districtData);
      }
    }

    return districts;
  }

  // TODO: Attempt to batch read
  /*  @Override
  public List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds) {
    List<DistrictData> districts = new ArrayList<>();
    BatchPolicy batchPolicy = new BatchPolicy();
    batchPolicy.maxConcurrentThreads = 10;
    BatchWritePolicy batchWritePolicy = new BatchWritePolicy();

    // Create an array of keys for batch retrieval
    Key[] keys = new Key[districtRefsIds.size()];
    for (int i = 0; i < districtRefsIds.size(); i++) {
      keys[i] =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(DistrictData.class),
              districtRefsIds.get(i));
    }

    */
  /*    List<Value> mapKeys =
      Arrays.asList(
          Value.get("warehouseRefId"),
          Value.get("name"),
          Value.get("address"),
          Value.get("salesTax"),
          Value.get("ytdBalance"),
          Value.get("customerRefsIds"),
          Value.get("orderRefsIds"));

  BatchResults batchResult =
      aerospikeClient.operate(
          batchPolicy,
          batchWritePolicy,
          keys,
          MapOperation.getByKeyList("PK", mapKeys, MapReturnType.VALUE));

  for (BatchRecord batchRecord : batchResult.records) {
    Record record = batchRecord.record;
    if (record != null) {
      System.out.format("Record: %s\\n", record.bins);
      DistrictData district = null;
      districts.add(district);
    }
  }*/
  /*
    ///////////// TRENNUNG
    // Batch retrieve the records associated with the keys
    // Record[] records = aerospikeTemplate.getAerospikeClient().get(batchPolicy, keys);

    Record[] records = aerospikeClient.get(batchPolicy, keys);
    // BatchResults batchResult = aerospikeClient.operate(batchPolicy, keys);
    // Process the retrieved records and extract DistrictData
    for (Record record : records) {
      if (record != null && !record.bins.isEmpty()) {
        // Manually map the bins to DistrictData

        Map<String, Object> addressMap = (Map<String, Object>) record.getMap("address");

        AddressData addressData =
            new AddressData(
                (String) addressMap.get("street1"),
                (String) addressMap.get("street2"),
                (String) addressMap.get("zipCode"),
                (String) addressMap.get("city"),
                (String) addressMap.get("state"));

        // Create the DistrictData instance
        DistrictData district =
            new DistrictData(
                record.getString("PK"),
                record.getString("warehouseRefId"),
                record.getString("name"),
                addressData, // Set the AddressData instance
                record.getDouble("salesTax"),
                record.getDouble("ytdBalance"));
        district.setCustomerRefsIds((List<String>) record.getList("customerRefsIds"));
        district.setOrderRefsIds((List<String>) record.getList("orderRefsIds"));

        districts.add(district);
      }
    }

    return districts;
  }*/

  @Override
  public void saveAll(Map<String, DistrictData> idsToDistricts) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToDistricts.forEach((id, district) -> aerospikeTemplate.save(district));
  }

  @Override
  public Map<String, DistrictData> getDistricts() {
    return aerospikeTemplate
        .findAll(DistrictData.class)
        .collect(Collectors.toMap(DistrictData::getId, district -> district));
  }
}

package de.uniba.dsg.wss.data.access;

import com.aerospike.client.*;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.AddressData;
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

  /*  @Override
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
  }*/

  @Override
  public List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds) {
    List<DistrictData> districts = new ArrayList<>();
    // 1.Step - Collect the keys/ids necessary to retrieve the objects
    Key[] keys = new Key[districtRefsIds.size()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(DistrictData.class),
              districtRefsIds.get(i));
    }

    // 2.Step - Retrieve districtData from Aerospike data model
    Record[] records = aerospikeTemplate.getAerospikeClient().get(null, keys);

    // 3.Step - Populate the list of districts
    for (int i = 0; i < records.length; i++) {
      Record record = records[i];
      if (record != null) {
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
                districtRefsIds.get(i), // Set the id using the districtRefsIds list
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
  }

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

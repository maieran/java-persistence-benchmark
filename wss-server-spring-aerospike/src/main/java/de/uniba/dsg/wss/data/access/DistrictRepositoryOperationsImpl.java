package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.convert.MappingAerospikeConverter;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class DistrictRepositoryOperationsImpl implements DistrictRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  private MappingAerospikeConverter aerospikeConverter;

  @Autowired
  public DistrictRepositoryOperationsImpl(
      AerospikeTemplate aerospikeTemplate, MappingAerospikeConverter aerospikeConverter) {
    this.aerospikeTemplate = aerospikeTemplate;
    this.aerospikeConverter = aerospikeConverter;
  }

  // TODO: HOW TO BATCH READ ?!
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

  @Override
  public void saveAll(Map<String, DistrictData> idsToDistricts) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToDistricts.forEach((id, district) -> aerospikeTemplate.save(district));
  }
}

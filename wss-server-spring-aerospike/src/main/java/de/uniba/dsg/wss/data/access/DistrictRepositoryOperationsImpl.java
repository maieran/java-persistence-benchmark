package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class DistrictRepositoryOperationsImpl implements DistrictRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  public DistrictRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds) {
    return null;
  }

  @Override
  public void saveAll(Map<String, DistrictData> idsToDistricts) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToDistricts.forEach((id, district) -> aerospikeTemplate.save(district));
  }
}

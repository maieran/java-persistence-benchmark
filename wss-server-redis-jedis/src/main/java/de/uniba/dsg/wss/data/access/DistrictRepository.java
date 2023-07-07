package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.Map;

public interface DistrictRepository {
  void saveAll(Map<String, DistrictData> idsToDistricts);

  DistrictData findById(String districtId);

  void save(DistrictData district);
}

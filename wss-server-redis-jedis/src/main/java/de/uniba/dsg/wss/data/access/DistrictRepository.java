package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;

public interface DistrictRepository {
  void saveAll(Map<String, DistrictData> idsToDistricts);

  DistrictData findById(String districtId);

  void save(DistrictData district);

  Map<String, DistrictData> getDistricts();

  List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds);
}

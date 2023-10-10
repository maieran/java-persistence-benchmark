package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;

public interface DistrictRepositoryOperations {
  List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds);

  void saveAll(Map<String, DistrictData> idsToDistrict);
}

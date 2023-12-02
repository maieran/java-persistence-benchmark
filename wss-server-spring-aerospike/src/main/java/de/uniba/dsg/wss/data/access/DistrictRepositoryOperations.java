package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;

/**
 * This interface provides custom operations specific to DistrictData in the Aerospike repository,
 * that are implemented in {@link DistrictRepositoryOperationsImpl} and are extended by {@link
 * DistrictRepository}.
 *
 * @author Andre Maier
 */
public interface DistrictRepositoryOperations {
  List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds);

  void saveAll(Map<String, DistrictData> idsToDistrict);

  Map<String, DistrictData> getDistricts();
}

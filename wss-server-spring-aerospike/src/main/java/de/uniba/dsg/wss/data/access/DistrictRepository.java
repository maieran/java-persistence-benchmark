package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.List;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aerospike repository for accessing and modifying {@link DistrictData districts}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to districts by {@link DistrictRepositoryOperations}.
 *
 * @author Andre Maier
 */
@Transactional(readOnly = true)
public interface DistrictRepository
    extends AerospikeRepository<DistrictData, String>, DistrictRepositoryOperations {
  void saveAll(Map<String, DistrictData> idsToDistricts);

  List<DistrictData> getDistrictsFromWarehouse(List<String> districtRefsIds);
}

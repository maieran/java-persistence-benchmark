package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DistrictRepository extends AerospikeRepository<DistrictData, Integer> {
  <S extends DistrictData> Iterable<S> saveAll(Map<String, DistrictData> idsToDistricts);
}

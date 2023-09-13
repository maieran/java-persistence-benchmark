package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(readOnly = true)
public interface DistrictRepository extends AerospikeRepository<DistrictData, Integer> {
    <S extends DistrictData> Iterable<S> saveAll(Map<String, DistrictData> idsToDistricts);

}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(readOnly = true)
public interface EmployeeRepository extends AerospikeRepository<EmployeeData, Integer> {
  EmployeeData findEmployeeByName(String username);

  <S extends EmployeeData> Iterable<S> saveAll(Map<String, EmployeeData> idsToDistricts);

}

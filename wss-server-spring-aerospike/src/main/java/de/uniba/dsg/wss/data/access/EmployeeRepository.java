package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EmployeeRepository
    extends AerospikeRepository<EmployeeData, String>, EmployeeRepositoryOperations {
  EmployeeData findEmployeeDataByUsername(String username);

  void saveAll(Map<String, EmployeeData> idsToEmployees);
}

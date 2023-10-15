package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aerospike repository for accessing and modifying {@link EmployeeData employees}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to employees by {@link EmployeeRepositoryOperations}.
 *
 * @author Andre Maier
 */
@Transactional(readOnly = true)
public interface EmployeeRepository
    extends AerospikeRepository<EmployeeData, String>, EmployeeRepositoryOperations {
  EmployeeData findEmployeeDataByUsername(String username);

  void saveAll(Map<String, EmployeeData> idsToEmployees);
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;

/**
 * This interface provides custom operations specific to EmployeeData in the Aerospike repository,
 * that are implemented in {@link EmployeeRepositoryOperationsImpl} and are extended by {@link
 * EmployeeRepository}.
 *
 * @author Andre Maier
 */
public interface EmployeeRepositoryOperations {
  EmployeeData findEmployeeDataByUsername(String username);

  void saveAll(Map<String, EmployeeData> idsToEmployees);
}

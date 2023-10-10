package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;

public interface EmployeeRepositoryOperations {
  EmployeeData findEmployeeDataByUsername(String username);

  void saveAll(Map<String, EmployeeData> idsToEmployees);
}

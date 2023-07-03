package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;

public interface EmployeeRepository {

  void saveAll(Map<String, EmployeeData> idsToEmployees);

  EmployeeData findEmployeeByName(String username);
}

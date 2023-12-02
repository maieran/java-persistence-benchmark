package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link EmployeeData employees}.
 *
 * @author Andre Maier
 */
public interface EmployeeRepository {

  void saveAll(Map<String, EmployeeData> idsToEmployees);

  EmployeeData findEmployeeByName(String username);

  void deleteAll();
}

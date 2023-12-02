package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.*;
import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link EmployeeRepositoryOperations} interface for
 * accessing and modifying {@link EmployeeData employees}.
 *
 * @author Andre Maier
 */
public class EmployeeRepositoryOperationsImpl implements EmployeeRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public EmployeeRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public EmployeeData findEmployeeDataByUsername(String username) {
    if (username.isBlank() || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be blank or empty");
    }

    List<EmployeeData> employeeDataList =
        aerospikeTemplate.findAll(EmployeeData.class).collect(Collectors.toList());

    return employeeDataList.stream()
        .filter(employee -> username.equals(employee.getUsername()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Retrieves an instance of {@link EmployeeData} from the Aerospike data model based on the
   * provided username. This method utilizes a secondary index on the 'username' attribute that is
   * defined with the annotation at {#link String username} in {@link EmployeeData}. Scan function
   * in {@link de.uniba.dsg.wss.AerospikeConfiguration} have to enabled in {@link
   * AerospikeDataSettings aerospikeDataSettings()} to activate the secondary index. For more
   * information on secondary index usage, see: https://docs.aerospike.com/server/features
   * https://docs.aerospike.com/server/architecture/secondary-index
   *
   * @param //username the username of the employee to retrieve
   * @return the EmployeeData instance corresponding to the provided username, or null if no
   *     employee is found with the given username
   */
  /*  public EmployeeData findEmployeeDataByUsername(String username) {
    Statement stmt = new Statement();
    stmt.setNamespace(aerospikeTemplate.getNamespace());
    stmt.setSetName(aerospikeTemplate.getSetName(EmployeeData.class));
    stmt.setFilter(Filter.equal("username", username));

    try (RecordSet recordSet = aerospikeTemplate.getAerospikeClient().query(null, stmt)) {
      while (recordSet.next()) {
        Key keyRecord = recordSet.getKey();
        EmployeeData employeeData =
            aerospikeTemplate.findById(keyRecord.userKey.toString(), EmployeeData.class);

        if (employeeData != null && username.equals(employeeData.getUsername())) {
          return employeeData;
        }
      }
    }

    return null; // Employee not found.
  }*/

  @Override
  public void saveAll(Map<String, EmployeeData> idsToEmployees) {

    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToEmployees.forEach((id, employeeData) -> aerospikeTemplate.save(employeeData));
  }
}

package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Key;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.*;
import de.uniba.dsg.wss.data.model.EmployeeData;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class EmployeeRepositoryOperationsImpl implements EmployeeRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public EmployeeRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  /**
   *  IS USING A SECONDARY INDEX: USERNAME
   *  https://docs.aerospike.com/server/features
   *  https://docs.aerospike.com/server/architecture/secondary-index
   * @param username
   * @return
   */
  public EmployeeData findEmployeeDataByUsername(String username) {
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
  }

  public EmployeeData findEmployeeDataById(String id) {
    Key key =
        new Key(
            aerospikeTemplate.getNamespace(), aerospikeTemplate.getSetName(EmployeeData.class), id);
    EmployeeData employeeData = aerospikeTemplate.findById(key, EmployeeData.class);
    return employeeData;
  }

  @Override
  public void saveAll(Map<String, EmployeeData> idsToEmployees) {
    /*    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    getIdsToEmployees.forEach((id, employeeData) -> aerospikeTemplate.save(employeeData));*/

    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;


    idsToEmployees.forEach((id, employeeData) -> aerospikeTemplate.save(employeeData));

    // aerospikeTemplate.getAerospikeClient().put((getIdsToEmployees.keySet()),
    // getIdsToEmployees.values());

  }
}

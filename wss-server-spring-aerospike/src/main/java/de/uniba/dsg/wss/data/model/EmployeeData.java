package de.uniba.dsg.wss.data.model;

import org.springframework.data.aerospike.mapping.Document;

/**
 * An employee of the wholesale supplier. Employees are the user group meant to perform the business
 * transactions, i.e. create new orders, or add new payments.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@Document(collection = "Employee")
public class EmployeeData extends PersonData {

  private final String title;

  // @Indexed(name = "username", type = IndexType.STRING)
  private final String username;

  private final String password;
  private final String role;
  // private final DistrictData districtRef;
  private final String districtRefId;

  public EmployeeData(
      String id,
      String firstName,
      String middleName,
      String lastName,
      AddressData address,
      String phoneNumber,
      String email,
      String title,
      String username,
      String password,
      String role,
      String districtRefId) {
    super(id, firstName, middleName, lastName, address, phoneNumber, email);
    this.title = title;
    this.username = username;
    this.password = password;
    this.role = role;
    this.districtRefId = districtRefId;
    // this.districtRef = districtRef;
  }

  public String getTitle() {
    return title;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getRole() {
    return role;
  }

  /*  public DistrictData getDistrictRef() {
    return districtRef;
  }*/
  public String getDistrictRefId() {
    return districtRefId;
  }
}

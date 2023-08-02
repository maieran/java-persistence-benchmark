package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.springframework.data.redis.core.RedisHash;

/**
 * An employee of the wholesale supplier. Employees are the user group meant to perform the business
 * transactions, i.e. create new orders, or add new payments.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@RedisHash("Employee")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class EmployeeData extends PersonData implements Serializable {

  private final String title;
  private final String username;
  private final String password;
  private final String role;
  private final String districtRefId;

  @JsonCreator
  public EmployeeData(
      @JsonProperty("id") String id,
      @JsonProperty("firstName") String firstName,
      @JsonProperty("middleName") String middleName,
      @JsonProperty("lastName") String lastName,
      @JsonProperty("address") AddressData address,
      @JsonProperty("phoneNumber") String phoneNumber,
      @JsonProperty("email") String email,
      @JsonProperty("title") String title,
      @JsonProperty("username") String username,
      @JsonProperty("password") String password,
      @JsonProperty("role") String role,
      @JsonProperty("districtRefId") String districtRefId) {
    super(id, firstName, middleName, lastName, address, phoneNumber, email);
    this.title = title;
    this.username = username;
    this.password = password;
    this.role = role;
    this.districtRefId = districtRefId;
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

  public String getDistrictRefId() {
    return districtRefId;
  }
}

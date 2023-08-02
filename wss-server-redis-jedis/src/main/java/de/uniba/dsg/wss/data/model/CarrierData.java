package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.springframework.data.redis.core.RedisHash;

/**
 * A carrier is responsible for fulfilling {@link OrderData orders} by delivering the ordered items
 * to the {@link CustomerData customer}, when retrieved via their unique identifier.
 *
 * @author Benedikt Full
 * @author Andre Maier
 */
@RedisHash("Carrier")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CarrierData extends BaseData implements Serializable {

  private final String name;
  private final String phoneNumber;
  private final AddressData address;

  @JsonCreator
  public CarrierData(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("phoneNumber") String phoneNumber,
      @JsonProperty("address") AddressData address) {
    super(id);
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public AddressData getAddress() {
    return address;
  }
}

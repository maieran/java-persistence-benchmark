package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Address")
public class AddressData {

  private final String street1;
  private final String street2;
  private final String zipCode;
  private final String city;
  private final String state;

  @JsonCreator
  public AddressData(
      @JsonProperty("street1") String street1,
      @JsonProperty("street2") String street2,
      @JsonProperty("zipCode") String zipCode,
      @JsonProperty("city") String city,
      @JsonProperty("state") String state) {
    this.street1 = street1;
    this.street2 = street2;
    this.zipCode = zipCode;
    this.city = city;
    this.state = state;
  }

  public String getStreet1() {
    return street1;
  }

  public String getStreet2() {
    return street2;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }
}

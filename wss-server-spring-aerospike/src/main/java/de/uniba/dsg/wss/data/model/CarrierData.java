package de.uniba.dsg.wss.data.model;

import org.springframework.data.aerospike.mapping.Document;

/**
 * A carrier is responsible for fulfilling {@link OrderData orders} by delivering the ordered items
 * to the {@link CustomerData customer}, when retrieved via their unique identifier.
 *
 * @author Benedikt Full
 * @author Andre Maier
 */
@Document(collection = "Carrier")
public class CarrierData extends BaseData {

  private final String name;
  private final String phoneNumber;
  private final AddressData address;

  public CarrierData(String id, String name, String phoneNumber, AddressData address) {
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

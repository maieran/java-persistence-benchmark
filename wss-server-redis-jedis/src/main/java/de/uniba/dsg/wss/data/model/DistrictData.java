package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("District")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DistrictData extends BaseData implements Serializable {

  // Object reference
  // private final WarehouseData warehouseRef;
  // Reference over the ID
  private final String warehouseRefId;

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private double yearToDateBalance;

  // Object reference
  // private final List<CustomerData> customerRefs;

  // Reference over the ID
  @JsonProperty("customerRefsIds")
  private List<String> customerRefsIds;

  // Object reference
  // private final Map<String, OrderData> orderRefs;
  @JsonProperty("orderRefsIds")
  private List<String> orderRefsIds;

  @JsonCreator
  public DistrictData(
      @JsonProperty("id") String id,
      @JsonProperty("warehouseRefId") String warehouseRefId,
      @JsonProperty("name") String name,
      @JsonProperty("address") AddressData address,
      @JsonProperty("salesTax") double salesTax,
      @JsonProperty("yearToDateBalance") double yearToDateBalance) {
    super(id);
    this.warehouseRefId = warehouseRefId;
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    this.yearToDateBalance = yearToDateBalance;
    this.customerRefsIds = new ArrayList<>();
    this.orderRefsIds = new ArrayList<>();
  }

  //  @JsonCreator
  //  public DistrictData(
  //      @JsonProperty("id") String id,
  //      @JsonProperty("warehouseRefId") String warehouseRefId,
  //      @JsonProperty("name ") String name,
  //      @JsonProperty("address ") AddressData address,
  //      @JsonProperty("salesTax") double salesTax,
  //      @JsonProperty("yearToDateBalance") double yearToDateBalance) {
  //    super(id);
  //    this.warehouseRefId = warehouseRefId;
  //    this.name = name;
  //    this.address = address;
  //    this.salesTax = salesTax;
  //    this.yearToDateBalance = yearToDateBalance;
  //    this.customerRefsIds = new ArrayList<>();
  //    this.orderRefsIds = new HashMap<>();
  //  }

  public String getWarehouseRefId() {
    return warehouseRefId;
  }

  public String getName() {
    return name;
  }

  public AddressData getAddress() {
    return address;
  }

  public double getSalesTax() {
    return salesTax;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }

  public List<String> getCustomerRefsIds() {
    return customerRefsIds;
  }

  public void setCustomerRefsIds(List<String> customerRefsIds) {
    this.customerRefsIds = customerRefsIds;
  }

  public List<String> getOrderRefsIds() {
    return orderRefsIds;
  }

  public void setOrderRefsIds(List<String> orderRefsIds) {
    this.orderRefsIds = orderRefsIds;
  }

  public void increaseYearToBalance(double amount) {
    this.yearToDateBalance += amount;
  }
}

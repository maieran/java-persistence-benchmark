package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Warehouse")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class WarehouseData extends BaseData implements Serializable {

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private double yearToDateBalance;

  // private final Map<String, DistrictData> districtRefs;
  // private final List<StockData> stockRefs;

  // reference over id
  @JsonProperty("stockRefsIds")
  private List<String> stockRefsIds;

  // reference over id
  @JsonProperty("districtRefsIds")
  private List<String> districtRefsIds;

  @JsonCreator
  public WarehouseData(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("address") AddressData address,
      @JsonProperty("salesTax") double salesTax,
      @JsonProperty("yearToDateBalance") double yearToDateBalance) {
    super(id);
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    this.yearToDateBalance = yearToDateBalance;

    /* TODO: We avoid the direct combination of Objects, since there is no possible way to attach them directly in Redis
       instead we use the ID and separate them
    */
    // districtRefs = new HashMap<>();
    // stockRefs = new ArrayList<>();

    stockRefsIds = new ArrayList<>();
    districtRefsIds = new ArrayList<>();
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

  public List<String> getDistrictRefsIds() {
    return this.districtRefsIds;
  }

  public void setDistrictRefsIds(List<String> districtRefsIds) {
    this.districtRefsIds = districtRefsIds;
  }

  public List<String> getStockRefsIds() {
    return this.stockRefsIds;
  }

  public void setStockRefsIds(List<String> stockRefsIds) {
    this.stockRefsIds = stockRefsIds;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }
}

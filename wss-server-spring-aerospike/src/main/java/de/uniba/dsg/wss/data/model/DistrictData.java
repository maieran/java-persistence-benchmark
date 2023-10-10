package de.uniba.dsg.wss.data.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.aerospike.mapping.Document;

@Document(collection = "District")
public class DistrictData extends BaseData {

  private final String warehouseRefId;

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private double yearToDateBalance;
  // Reference over the ID

  private List<String> customerRefsIds;
  // Reference over the ID

  private List<String> orderRefsIds;

  public DistrictData(
      String id,
      String warehouseRefId,
      String name,
      AddressData address,
      double salesTax,
      double yearToDateBalance) {
    super(id);
    this.warehouseRefId = warehouseRefId;
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    this.yearToDateBalance = yearToDateBalance;
    this.customerRefsIds = new ArrayList<>();
    this.orderRefsIds = new ArrayList<>();
  }

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

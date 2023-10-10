package de.uniba.dsg.wss.data.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.aerospike.mapping.Document;

@Document(collection = "Warehouse")
public class WarehouseData extends BaseData {

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private double yearToDateBalance;
  // reference over id

  private List<String> stockRefsIds;
  // reference over id

  private List<String> districtRefsIds;

  public WarehouseData(
      String id, String name, AddressData address, double salesTax, double yearToDateBalance) {
    super(id);
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    this.yearToDateBalance = yearToDateBalance;
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

  public void increaseYearToBalance(double amount) {
    this.yearToDateBalance += amount;
  }
}

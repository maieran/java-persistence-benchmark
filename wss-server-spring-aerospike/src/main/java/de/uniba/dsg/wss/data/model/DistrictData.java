package de.uniba.dsg.wss.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistrictData extends BaseData {

  private final WarehouseData warehouseRef;

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private double yearToDateBalance;

  private final List<CustomerData> customerRefs;
  private final Map<String, OrderData> orderRefs;

  public DistrictData(
      String id,
      WarehouseData warehouse,
      String name,
      AddressData address,
      double salesTax,
      double yearToDateBalance) {
    super(id);
    this.warehouseRef = warehouse;
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    this.yearToDateBalance = yearToDateBalance;
    this.customerRefs = new ArrayList<>();
    this.orderRefs = new ConcurrentHashMap<>();
  }

  public WarehouseData getWarehouse() {
    return warehouseRef;
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

  public List<CustomerData> getCustomers() {
    return this.customerRefs;
  }

  public Map<String, OrderData> getOrders() {
    return this.orderRefs;
  }

  public void increaseYearToBalance(double amount) {

    this.yearToDateBalance += amount;
  }

  public double getYearToDateBalance() {

    return yearToDateBalance;
  }
}

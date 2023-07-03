package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisDataModel
    implements DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private final Map<String, ProductData> products;
  private final Map<String, WarehouseData> warehouses;
  private final Map<String, EmployeeData> employees;
  private final Map<String, StockData> stocks;
  private final Map<String, DistrictData> districts;
  private final Map<String, CarrierData> carriers;
  private final Map<String, CustomerData> customers;
  private final Map<String, OrderData> orders;
  private final Map<String, OrderItemData> orderItems;
  private final Map<String, PaymentData> payments;
  private final Stats stats;

  public RedisDataModel(
      Map<String, ProductData> products,
      Map<String, WarehouseData> warehouses,
      Map<String, EmployeeData> employees,
      Map<String, DistrictData> districts,
      Map<String, StockData> stocks,
      Map<String, CarrierData> carriers,
      Map<String, CustomerData> customers,
      Map<String, OrderData> orders,
      Map<String, OrderItemData> orderItems,
      Map<String, PaymentData> payments,
      Stats stats) {
    this.products = products;
    this.warehouses = warehouses;
    this.employees = employees;
    this.districts = districts;
    this.stocks = stocks;
    this.carriers = carriers;
    this.customers = customers;
    this.orders = orders;
    this.orderItems = orderItems;
    this.payments = payments;
    this.stats = stats;
  }

  public Map<String, ProductData> getIdsToProducts() {
    return products;
  }

  public Map<String, WarehouseData> getIdsToWarehouses() {
    return warehouses;
  }

  public Map<String, EmployeeData> getIdsToEmployees() {
    return employees;
  }

  public Map<String, DistrictData> getIdsToDistricts() {
    return districts;
  }

  public Map<String, StockData> getIdsToStocks() {
    return stocks;
  }

  public Map<String, CarrierData> getIdsToCarriers() {
    return carriers;
  }

  public Map<String, CustomerData> getIdsToCustomers() {
    return customers;
  }

  public Map<String, OrderData> getIdsToOrders() {
    return orders;
  }

  public Map<String, OrderItemData> getIdsToOrderItems() {
    return orderItems;
  }

  public Map<String, PaymentData> getIdsToPayments() {
    return payments;
  }

  @Override
  public List<ProductData> getProducts() {
    return new ArrayList<>(products.values());
  }

  @Override
  public List<WarehouseData> getWarehouses() {
    return new ArrayList<>(warehouses.values());
  }

  @Override
  public List<EmployeeData> getEmployees() {
    return new ArrayList<>(employees.values());
  }

  @Override
  public List<CarrierData> getCarriers() {
    return new ArrayList<>(carriers.values());
  }

  @Override
  public Stats getStats() {
    return stats;
  }
}

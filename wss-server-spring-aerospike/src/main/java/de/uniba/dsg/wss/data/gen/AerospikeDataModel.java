package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for storing the converted model data produced by {@link AerospikeDataConverter} instances.
 *
 * <p>While this model implements the {@link DataModel} interface, the primary access to the model
 * data is facilitated through the {@code getIdsToXxx()} methods, which return maps containing model
 * objects mapped to their unique identifiers.
 *
 * @author Benedikt Full
 * @author Andre Maier
 */
public class AerospikeDataModel
    implements DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private final Map<String, ProductData> products;
  private final Map<String, WarehouseData> warehouses;
  private final Map<String, DistrictData> districts;
  private final Map<String, EmployeeData> employees;
  private final Map<String, StockData> stocks;
  private final Map<String, CarrierData> carriers;
  private final Map<String, CustomerData> customers;
  private final Map<String, PaymentData> payments;
  private final Map<String, OrderItemData> orderItems;
  private final Map<String, OrderData> orders;
  private final Stats stats;

  public AerospikeDataModel(
      Map<String, ProductData> products,
      Map<String, WarehouseData> warehouses,
      Map<String, DistrictData> districts,
      Map<String, EmployeeData> employees,
      Map<String, StockData> stocks,
      Map<String, CarrierData> carriers,
      Map<String, CustomerData> customers,
      Map<String, PaymentData> payments,
      Map<String, OrderItemData> orderItems,
      Map<String, OrderData> orders,
      Stats stats) {
    this.products = products;
    this.warehouses = warehouses;
    this.districts = districts;
    this.employees = employees;
    this.stocks = stocks;
    this.carriers = carriers;
    this.customers = customers;
    this.payments = payments;
    this.orders = orders;
    this.orderItems = orderItems;
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

  public Map<String, StockData> getIdsToStocks() {
    return stocks;
  }

  public Map<String, DistrictData> getIdsToDistricts() {
    return districts;
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

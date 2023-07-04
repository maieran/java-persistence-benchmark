package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.model.*;
import de.uniba.dsg.wss.data.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedisDataConverter
    implements DataConverter<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private static final Logger LOG = LogManager.getLogger(RedisDataConverter.class);

  @Override
  public RedisDataModel convert(DataModel<Product, Warehouse, Employee, Carrier> model) {
    // Create model objects by converting provided template
    Stopwatch stopwatch = new Stopwatch().start();

    // TODO: No need to rework the references , since these objects contain simple data types
    Map<String, ProductData> products = convertProducts(model.getProducts());
    Map<String, CarrierData> carriers = convertCarriers(model.getCarriers());

    // TODO: We need to include here the ID's to avoid circular references and therefore
    // StackOverflowError
    Map<String, WarehouseData> warehouses =
        convertWarehouses(model.getWarehouses()); // TODO: FERTIG
    Map<String, StockData> stocks =
        convertStocks(model.getWarehouses(), warehouses, products); // TODO: FERTIG
    Map<String, DistrictData> districts =
        convertDistricts(model.getWarehouses(), warehouses); // TODO: FERTIG
    Map<String, EmployeeData> employees = convertEmployees(model.getEmployees()); // TODO: Fertig
    Map<String, CustomerData> customers =
        convertCustomers(model.getWarehouses(), districts); // TODO: FERTIG
    Map<String, OrderData> orders =
        convertOrders(model.getWarehouses(), districts, customers, carriers); // TODO: Fertig
    Map<String, OrderItemData> orderItems =
        convertOrderItems(model.getWarehouses(), warehouses, products, orders); // TODO: ids
    Map<String, PaymentData> payments =
        convertPayments(model.getWarehouses(), customers); // TODO: ids
    stopwatch.stop();

    // Create summary data
    Stats stats = new Stats();
    stats.setTotalModelObjectCount(model.getStats().getTotalModelObjectCount());
    stats.setDurationMillis(stopwatch.getDurationMillis());
    stats.setDuration(stopwatch.getDuration());

    // Wrap everything in model instance
    RedisDataModel generatedModel =
        new RedisDataModel(
            products,
            warehouses,
            employees,
            districts,
            stocks,
            carriers,
            customers,
            orders,
            orderItems,
            payments,
            stats);

    LOG.info("Converted model data to Redis data, took {}", stopwatch.getDuration());

    return generatedModel;
  }

  private Map<String, ProductData> convertProducts(List<Product> ps) {
    Map<String, ProductData> products = new HashMap<>();
    for (Product p : ps) {
      ProductData product =
          new ProductData(p.getId(), p.getImagePath(), p.getName(), p.getPrice(), p.getData());
      products.put(product.getId(), product);
    }
    LOG.debug("Converted {} products", products.size());
    return products;
  }

  private Map<String, CarrierData> convertCarriers(List<Carrier> cs) {
    Map<String, CarrierData> carriers = new HashMap<>();
    for (Carrier c : cs) {
      CarrierData carrier =
          new CarrierData(c.getId(), c.getName(), c.getPhoneNumber(), address(c.getAddress()));
      carriers.put(carrier.getId(), carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  private Map<String, WarehouseData> convertWarehouses(List<Warehouse> ws) {
    Map<String, WarehouseData> warehouses = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseData warehouse =
          new WarehouseData(
              w.getId(),
              w.getName(),
              address(w.getAddress()),
              w.getSalesTax(),
              w.getYearToDateBalance());
      // NEW relaxing the concurrency thing here, since at the data generation step, the procedure
      // is implemented single threaded

      warehouses.put(warehouse.getId(), warehouse);
    }
    LOG.debug("Converted {} warehouses", warehouses.size());
    return warehouses;
  }

  private Map<String, StockData> convertStocks(
      List<Warehouse> ws,
      Map<String, WarehouseData> warehouses,
      Map<String, ProductData> products) {

    Map<String, StockData> stocks = new HashMap<>();

    for (Warehouse warehouseBase : ws) {

      WarehouseData warehouse = warehouses.get(warehouseBase.getId());

      for (Stock stockBase : warehouseBase.getStocks()) {

        // create stock data
        StockData stockData = convertStock(stockBase, warehouse, products);

        // add stock via Id to warehouse
        warehouse.getStockRefsIds().add(stockData.getId());

        stocks.put(stockData.getId(), stockData);
      }
    }

    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private StockData convertStock(
      Stock s, WarehouseData warehouse, Map<String, ProductData> products) {
    return new StockData(
        s.getId(),
        warehouse.getId(),
        s.getProduct().getId(),
        s.getQuantity(),
        s.getYearToDateBalance(),
        s.getOrderCount(),
        s.getRemoteCount(),
        s.getData(),
        s.getDist01(),
        s.getDist02(),
        s.getDist03(),
        s.getDist04(),
        s.getDist05(),
        s.getDist06(),
        s.getDist07(),
        s.getDist08(),
        s.getDist09(),
        s.getDist10());
  }

  /**
   * Districts are now also added to the warehouse (bidirectional relationship)
   *
   * @param ws warehouses to be converted
   * @param warehouses the already converted warehouses
   * @return a map with district ids as keys and districts as values
   */
  private Map<String, DistrictData> convertDistricts(
      List<Warehouse> ws, Map<String, WarehouseData> warehouses) {
    Map<String, DistrictData> districts = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseData warehouse = warehouses.get(w.getId());
      // Map<String, DistrictData> districtsForWarehouse = warehouse.getDistricts();

      for (District d : w.getDistricts()) {
        // referential integrity...
        DistrictData districtData = convertDistrict(d, warehouse);
        // districtsForWarehouse.put(districtData.getId(), districtData);

        districts.put(districtData.getId(), districtData);
        warehouse.getDistrictRefsIds().add(d.getId());
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  private DistrictData convertDistrict(District d, WarehouseData warehouse) {
    return new DistrictData(
        d.getId(),
        warehouse.getId(),
        d.getName(),
        address(d.getAddress()),
        d.getSalesTax(),
        d.getYearToDateBalance());
  }

  private Map<String, EmployeeData> convertEmployees(List<Employee> es) {
    Map<String, EmployeeData> employees = new HashMap<>();
    for (Employee e : es) {
      EmployeeData employee =
          new EmployeeData(
              e.getId(),
              e.getFirstName(),
              e.getMiddleName(),
              e.getLastName(),
              address(e.getAddress()),
              e.getPhoneNumber(),
              e.getEmail(),
              e.getTitle(),
              e.getUsername(),
              e.getPassword(),
              e.getRole(),
              e.getDistrict().getId());

      employees.put(employee.getUsername(), employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private Map<String, CustomerData> convertCustomers(
      List<Warehouse> ws, Map<String, DistrictData> districts) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return convertCustomerToCustomerData(cs, districts);
  }

  private Map<String, CustomerData> convertCustomerToCustomerData(
      List<Customer> cs, Map<String, DistrictData> districts) {
    Map<String, CustomerData> customers = new HashMap<>();
    for (Customer c : cs) {

      // TODO: Check if it is needed ?
      HashMap<String, String> ordersIds = fetchOrderIds(c.getOrders());
      List<String> paymentIds = fetchPaymentIds(c.getPayments());

      CustomerData customer =
          new CustomerData(
              c.getId(),
              c.getFirstName(),
              c.getMiddleName(),
              c.getLastName(),
              address(c.getAddress()),
              c.getPhoneNumber(),
              c.getEmail(),
              // referential integrity
              c.getDistrict().getId(),
              // TODO: Check if it is needed ?
              // ordersIds,
              // paymentIds,
              c.getSince(),
              c.getCredit(),
              c.getCreditLimit(),
              c.getDiscount(),
              c.getBalance(),
              c.getYearToDatePayment(),
              c.getPaymentCount(),
              c.getDeliveryCount(),
              c.getData());

      customers.put(customer.getId(), customer);
      // referential integrity
      districts.get(customer.getDistrictRefId()).getCustomerRefsIds().add(customer.getId());
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private HashMap<String, String> fetchOrderIds(List<Order> orders) {
    if (orders != null) {
      HashMap<String, String> ordersIds = new HashMap<>();
      for (Order order : orders) {
        ordersIds.put(order.getId(), order.getId());
      }

      return ordersIds;
    }
    return null;
  }

  private List<String> fetchPaymentIds(List<Payment> payments) {
    List<String> paymentRefsIds = new ArrayList<>();
    for (Payment payment : payments) {
      paymentRefsIds.add(payment.getId());
    }
    return paymentRefsIds;
  }

  private Map<String, OrderData> convertOrders(
      List<Warehouse> ws,
      Map<String, DistrictData> districts,
      Map<String, CustomerData> customers,
      Map<String, CarrierData> carriers) {
    Map<String, OrderData> orders = new HashMap<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        DistrictData district = districts.get(d.getId());
        for (Order o : d.getOrders()) {
          OrderData order =
              new OrderData(
                  o.getId(),
                  district.getId(),
                  // referential integrity
                  // customers.get(o.getCustomer().getId()),
                  o.getCustomer().getId(),
                  // referential integrity
                  o.getCarrier() == null ? null : o.getCarrier().getId(),
                  o.getEntryDate(),
                  o.getItemCount(),
                  o.isAllLocal(),
                  o.isFulfilled());

          orders.put(order.getId(), order);
          // referential integrity
          // district.getOrderRefsIds().put(order.getId(), order.getId());
          district.getOrderRefsIds().add(order.getId());
          customers
              .get(order.getCustomerRefId())
              .getOrderRefsIds()
              .put(order.getId(), order.getId());
        }
      }
    }
    return orders;
  }

  private Map<String, OrderItemData> convertOrderItems(
      List<Warehouse> ws,
      Map<String, WarehouseData> warehouses,
      Map<String, ProductData> products,
      Map<String, OrderData> orders) {
    Map<String, OrderItemData> ois = new HashMap<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        for (Order o : d.getOrders()) {
          OrderData order = orders.get(o.getId());
          for (OrderItem i : o.getItems()) {
            OrderItemData item =
                new OrderItemData(
                    i.getId(),
                    order.getId(),
                    i.getProduct().getId(),
                    i.getSupplyingWarehouse().getId(),
                    i.getNumber(),
                    i.getDeliveryDate(),
                    i.getQuantity(),
                    0, // ok for this initial values
                    i.getAmount(),
                    i.getDistInfo());

            ois.put(i.getId(), item);
            // referential integrity
            order.getItemsIds().add(item.getId());
          }
        }
      }
    }
    return ois;
  }

  private Map<String, PaymentData> convertPayments(
      List<Warehouse> ws, Map<String, CustomerData> customers) {
    List<Payment> ps =
        ws.parallelStream()
            .flatMap(
                w ->
                    w.getDistricts().parallelStream()
                        .flatMap(
                            d ->
                                d.getCustomers().parallelStream()
                                    .flatMap(c -> c.getPayments().stream())))
            .collect(Collectors.toList());

    Map<String, PaymentData> payments = new HashMap<>();
    for (Payment p : ps) {
      PaymentData payment =
          new PaymentData(
              p.getId(), p.getCustomer().getId(), p.getDate(), p.getAmount(), p.getData());

      payments.put(p.getId(), payment);
      // referential integrity
      customers.get(p.getCustomer().getId()).getPaymentRefsIds().add(payment.getId());
    }

    return payments;
  }

  private static AddressData address(Address a) {
    return new AddressData(
        a.getStreet1(), a.getStreet2(), a.getZipCode(), a.getCity(), a.getState());
  }
}

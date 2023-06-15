package de.uniba.dsg.wss.data.gen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JacksonParser {

  private static final Logger LOG = LogManager.getLogger(JacksonParser.class);
  private List<Customer> customers = new ArrayList<>();
  private final HashMap<String, District> districtMap = new HashMap<>();

  //  public void serializeToJSON(Object data, String filePath, ObjectMapper objectMapper)
  //      throws IOException {
  //    File file = new File(filePath);
  //
  //    if (!file.exists()) {
  //      Stopwatch stopwatch = new Stopwatch().start();
  //      try (FileOutputStream fileOutputStream = new FileOutputStream(file);
  //          BufferedOutputStream bufferedOutputStream = new
  // BufferedOutputStream(fileOutputStream)) {
  //
  //        JsonFactory jsonFactory = new JsonFactory();
  //        JsonGenerator jsonGenerator = jsonFactory.createGenerator(bufferedOutputStream);
  //
  //        // jsonGenerator.setCodec(objectMapper);
  //        // DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
  //
  //        // jsonGenerator.setPrettyPrinter(printer);
  //        jsonGenerator.writeObject(data);
  //        jsonGenerator.flush();
  //        jsonGenerator.close();
  //
  //        stopwatch.stop();
  //
  //        LOG.info("Serialized data to file: {} and took {}", filePath, stopwatch.getDuration());
  //
  //      } catch (IOException e) {
  //        LOG.error("Error occurred while serializing data to file: {}", filePath);
  //        throw e;
  //      }
  //    } else {
  //      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
  //    }
  //  }

  // TODO: Dokumentation
  public void serializeProductsToJSON(List<Product> products, String filePath) throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      Stopwatch stopwatch = new Stopwatch().start();

      JsonFactory jsonFactory = new JsonFactory();
      try (OutputStream outputStream = new FileOutputStream(filePath);
          JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("products");
        jsonGenerator.writeStartArray();

        for (Product product : products) {
          writeProduct(jsonGenerator, product);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        stopwatch.stop();

        LOG.info("Serialized data to file: {} and took {}", filePath, stopwatch.getDuration());

      } catch (IOException e) {
        LOG.error("Error occurred while serializing data to file: {}", filePath);
        throw e;
      }
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  private void writeProduct(JsonGenerator jsonGenerator, Product product) throws IOException {

    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", product.getId());
    jsonGenerator.writeStringField("imagePath", product.getImagePath());
    jsonGenerator.writeStringField("name", product.getName());
    jsonGenerator.writeNumberField("price", product.getPrice());
    jsonGenerator.writeStringField("data", product.getData());

    jsonGenerator.writeEndObject();
  }

  // TODO: Dokumentation
  public void serializeStatsToJSON(Stats stats, String filePath) throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      Stopwatch stopwatch = new Stopwatch().start();

      JsonFactory jsonFactory = new JsonFactory();

      try (OutputStream outputStream = new FileOutputStream(filePath);
          JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("totalModelObjectCount", stats.getTotalModelObjectCount());
        jsonGenerator.writeNumberField("durationMillis", stats.getDurationMillis());
        jsonGenerator.writeStringField("duration", stats.getDuration());

        jsonGenerator.writeEndObject();
        stopwatch.stop();

        LOG.info("Serialized data to file: {} and took {}", filePath, stopwatch.getDuration());

      } catch (IOException e) {
        LOG.error("Error occurred while serializing data to file: {}", filePath);
        throw e;
      }
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  // TODO: Dokumentation
  public void serializeCarriersToJSON(List<Carrier> carriers, String filePath) throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      Stopwatch stopwatch = new Stopwatch().start();
      JsonFactory jsonFactory = new JsonFactory();

      try (OutputStream outputStream = new FileOutputStream(filePath);
          JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("carriers");
        jsonGenerator.writeStartArray();

        for (Carrier carrier : carriers) {
          writeCarrier(jsonGenerator, carrier);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        stopwatch.stop();

        LOG.info("Serialized data to file: {} and took {}", filePath, stopwatch.getDuration());

      } catch (IOException e) {
        LOG.error("Error occurred while serializing data to file: {}", filePath);
        throw e;
      }
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  private void writeCarrier(JsonGenerator jsonGenerator, Carrier carrier) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", carrier.getId());
    jsonGenerator.writeStringField("name", carrier.getName());
    jsonGenerator.writeStringField("phoneNumber", carrier.getPhoneNumber());
    jsonGenerator.writeFieldName("address");
    writeAddress(jsonGenerator, carrier.getAddress());

    jsonGenerator.writeEndObject();
  }

  // TODO: Dokumentation
  public void serializeEmployeesToJSON(List<Employee> employees, String filePath)
      throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      Stopwatch stopwatch = new Stopwatch().start();
      JsonFactory jsonFactory = new JsonFactory();

      try (OutputStream outputStream = new FileOutputStream(filePath);
          JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("employees");
        jsonGenerator.writeStartArray();

        for (Employee employee : employees) {
          writeEmployee(jsonGenerator, employee);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();

        stopwatch.stop();

        LOG.info("Serialized data to file: {} and took {}", filePath, stopwatch.getDuration());

      } catch (IOException e) {
        LOG.error("Error occurred while serializing data to file: {}", filePath);
        throw e;
      }
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  public void writeEmployee(JsonGenerator jsonGenerator, Employee employee) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", employee.getId());
    jsonGenerator.writeStringField("firstName", employee.getFirstName());
    jsonGenerator.writeStringField("middleName", employee.getMiddleName());
    jsonGenerator.writeStringField("lastName", employee.getLastName());
    jsonGenerator.writeFieldName("address");
    writeAddress(jsonGenerator, employee.getAddress());
    jsonGenerator.writeStringField("phoneNumber", employee.getPhoneNumber());
    jsonGenerator.writeStringField("email", employee.getEmail());
    jsonGenerator.writeStringField("title", employee.getTitle());
    jsonGenerator.writeStringField("username", employee.getUsername());
    jsonGenerator.writeStringField("password", employee.getPassword());
    jsonGenerator.writeStringField("role", employee.getRole());
    jsonGenerator.writeStringField("district", employee.getDistrict().getId());

    jsonGenerator.writeEndObject();
  }

  // TODO: Dokumentation
  public void serializeWarehousesToJSON(List<Warehouse> warehouses, String filePath)
      throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      Stopwatch stopwatch = new Stopwatch().start();

      JsonFactory jsonFactory = new JsonFactory();
      try (OutputStream outputStream = new FileOutputStream(filePath);
          JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("warehouses");
        jsonGenerator.writeStartArray();

        for (Warehouse warehouse : warehouses) {
          writeWarehouse(jsonGenerator, warehouse);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        stopwatch.stop();

        LOG.info("Serialized data to file: {} and took {}", filePath, stopwatch.getDuration());

      } catch (IOException e) {
        LOG.error("Error occurred while serializing data to file: {}", filePath);
        throw e;
      }
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  private void writeWarehouse(JsonGenerator jsonGenerator, Warehouse warehouse) throws IOException {

    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", warehouse.getId());
    jsonGenerator.writeStringField("name", warehouse.getName());
    jsonGenerator.writeFieldName("address");
    writeAddress(jsonGenerator, warehouse.getAddress());
    jsonGenerator.writeFieldName("districts");
    writeDistricts(jsonGenerator, warehouse.getDistricts());
    jsonGenerator.writeFieldName("stocks");
    writeStocks(jsonGenerator, warehouse.getStocks());
    jsonGenerator.writeNumberField("salesTax", warehouse.getSalesTax());
    jsonGenerator.writeNumberField("yearToDateBalance", warehouse.getYearToDateBalance());

    jsonGenerator.writeEndObject();
  }

  private void writeStocks(JsonGenerator jsonGenerator, List<Stock> stocks) throws IOException {
    jsonGenerator.writeStartArray();

    for (Stock stock : stocks) {
      writeStock(jsonGenerator, stock);
    }

    jsonGenerator.writeEndArray();
  }

  private void writeStock(JsonGenerator jsonGenerator, Stock stock) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", stock.getId());
    jsonGenerator.writeFieldName("product");
    writeProduct(jsonGenerator, stock.getProduct());
    jsonGenerator.writeNumberField("quantity", stock.getQuantity());
    jsonGenerator.writeStringField("warehouse", stock.getWarehouse().getId());
    jsonGenerator.writeNumberField("yearToDateBalance", stock.getYearToDateBalance());
    jsonGenerator.writeNumberField("orderCounter", stock.getOrderCount());
    jsonGenerator.writeNumberField("remoteCount", stock.getRemoteCount());
    jsonGenerator.writeStringField("data", stock.getData());

    jsonGenerator.writeStringField("dist01", stock.getDist01());
    jsonGenerator.writeStringField("dist02", stock.getDist02());
    jsonGenerator.writeStringField("dist03", stock.getDist03());
    jsonGenerator.writeStringField("dist04", stock.getDist04());
    jsonGenerator.writeStringField("dist05", stock.getDist05());
    jsonGenerator.writeStringField("dist06", stock.getDist06());
    jsonGenerator.writeStringField("dist07", stock.getDist07());
    jsonGenerator.writeStringField("dist08", stock.getDist08());
    jsonGenerator.writeStringField("dist09", stock.getDist09());
    jsonGenerator.writeStringField("dist10", stock.getDist10());

    jsonGenerator.writeEndObject();
  }

  private void writeDistricts(JsonGenerator jsonGenerator, List<District> districts)
      throws IOException {

    jsonGenerator.writeStartArray();

    for (District district : districts) {
      writeDistrict(jsonGenerator, district);
    }

    jsonGenerator.writeEndArray();
  }

  private void writeDistrict(JsonGenerator jsonGenerator, District district) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", district.getId());
    jsonGenerator.writeStringField("warehouse", district.getWarehouse().getId());
    jsonGenerator.writeFieldName("customers");
    writeCustomers(jsonGenerator, district.getCustomers());
    jsonGenerator.writeFieldName("orders");
    writeOrders(jsonGenerator, district.getOrders());
    jsonGenerator.writeStringField("name", district.getName());
    jsonGenerator.writeFieldName("address");
    writeAddress(jsonGenerator, district.getAddress());
    jsonGenerator.writeNumberField("salesTax", district.getSalesTax());
    jsonGenerator.writeNumberField("yearToDateBalance", district.getYearToDateBalance());

    jsonGenerator.writeEndObject();
  }

  private void writeCustomers(JsonGenerator jsonGenerator, List<Customer> customers)
      throws IOException {
    jsonGenerator.writeStartArray();

    for (Customer customer : customers) {
      writeCustomer(jsonGenerator, customer);
    }

    jsonGenerator.writeEndArray();
  }

  private void writeCustomer(JsonGenerator jsonGenerator, Customer customer) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", customer.getId());
    jsonGenerator.writeStringField("firstName", customer.getFirstName());
    jsonGenerator.writeStringField("middleName", customer.getMiddleName());
    jsonGenerator.writeStringField("lastName", customer.getLastName());
    jsonGenerator.writeFieldName("address");
    writeAddress(jsonGenerator, customer.getAddress());
    jsonGenerator.writeStringField("phoneNumber", customer.getPhoneNumber());
    jsonGenerator.writeStringField("email", customer.getEmail());
    jsonGenerator.writeStringField("district", customer.getDistrict().getId());
    // TODO: check how the deserialization of localdateTime will be

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    String formattedSince = customer.getSince().format(formatter).substring(0, 19);
    ;
    jsonGenerator.writeStringField("since", formattedSince);
    jsonGenerator.writeFieldName("payments");
    writePayments(jsonGenerator, customer.getPayments());
    // TODO:avoiding circular inference
    jsonGenerator.writeStringField("orders", null);
    jsonGenerator.writeStringField("credit", customer.getCredit());
    jsonGenerator.writeNumberField("creditLimit", customer.getCreditLimit());
    jsonGenerator.writeNumberField("discount", customer.getDiscount());
    jsonGenerator.writeNumberField("balance", customer.getBalance());
    jsonGenerator.writeNumberField("yearToDatePayment", customer.getYearToDatePayment());
    jsonGenerator.writeNumberField("paymentCount", customer.getPaymentCount());
    jsonGenerator.writeNumberField("deliveryCount", customer.getDeliveryCount());
    jsonGenerator.writeStringField("data", customer.getData());

    jsonGenerator.writeEndObject();
  }

  private void writeOrders(JsonGenerator jsonGenerator, List<Order> orders) throws IOException {

    jsonGenerator.writeStartArray();

    for (Order order : orders) {
      writeOrder(jsonGenerator, order);
    }

    jsonGenerator.writeEndArray();
  }

  private void writeOrder(JsonGenerator jsonGenerator, Order order) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", order.getId());
    jsonGenerator.writeStringField("district", order.getDistrict().getId());
    jsonGenerator.writeStringField("customer", order.getCustomer().getId());
    jsonGenerator.writeStringField("entryDate", order.getEntryDate().toString());

    if (order.getCarrier() == null) {
      jsonGenerator.writeStringField("carrier", null);
    } else {
      jsonGenerator.writeFieldName("carrier");
      writeCarrier(jsonGenerator, order.getCarrier());
    }

    // TODO:Breaking the circular references
    jsonGenerator.writeFieldName("items");
    writeOrderItems(jsonGenerator, order.getItems());

    jsonGenerator.writeNumberField("itemCount", order.getItemCount());
    jsonGenerator.writeBooleanField("allLocal", order.isAllLocal());
    jsonGenerator.writeBooleanField("fulfilled", order.isFulfilled());

    jsonGenerator.writeEndObject();
  }

  private void writeOrderItems(JsonGenerator jsonGenerator, List<OrderItem> orderItems)
      throws IOException {

    jsonGenerator.writeStartArray();

    for (OrderItem orderItem : orderItems) {
      writeOrderItem(jsonGenerator, orderItem);
    }

    jsonGenerator.writeEndArray();
  }

  private void writeOrderItem(JsonGenerator jsonGenerator, OrderItem orderItem) throws IOException {

    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", orderItem.getId());
    // TODO:We need to draw references again when building java objects
    // jsonGenerator.writeFieldName("order");
    // writeOrderFields(jsonGenerator, orderItem.getOrder());
    jsonGenerator.writeStringField("order", orderItem.getOrder().getId());
    jsonGenerator.writeNumberField("number", orderItem.getNumber());
    jsonGenerator.writeFieldName("product");
    writeProduct(jsonGenerator, orderItem.getProduct());
    jsonGenerator.writeStringField("supplyingWarehouse", orderItem.getSupplyingWarehouse().getId());
    // TODO: check how the deserialization of localdateTime will be

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    // String formattedSince = orderItem.getDeliveryDate().format(formatter);
    if (orderItem.getDeliveryDate() == null) {
      jsonGenerator.writeStringField("deliveryDate", null);
    } else {

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      String formattedDeliveryDate = orderItem.getDeliveryDate().format(formatter).substring(0, 19);
      jsonGenerator.writeStringField("deliveryDate", formattedDeliveryDate);
    }

    // jsonGenerator.writeStringField("deliveryDate", orderItem.getDeliveryDate().toString());
    jsonGenerator.writeNumberField("quantity", orderItem.getQuantity());
    jsonGenerator.writeNumberField("amount", orderItem.getAmount());
    jsonGenerator.writeStringField("distInfo", orderItem.getDistInfo());

    jsonGenerator.writeEndObject();
  }

  private void writePayments(JsonGenerator jsonGenerator, List<Payment> payments)
      throws IOException {
    jsonGenerator.writeStartArray();

    for (Payment payment : payments) {
      writePayment(jsonGenerator, payment);
    }

    jsonGenerator.writeEndArray();
  }

  private void writePayment(JsonGenerator jsonGenerator, Payment payment) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", payment.getId());
    jsonGenerator.writeStringField("customer", payment.getCustomer().getId());
    // TODO: check how the deserialization of localdateTime will be
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    String formattedDate = payment.getDate().format(formatter).substring(0, 19);
    ;
    jsonGenerator.writeStringField("date", formattedDate);
    jsonGenerator.writeStringField("district", payment.getDistrict().getId());
    jsonGenerator.writeNumberField("amount", payment.getAmount());
    jsonGenerator.writeStringField("data", payment.getData());

    jsonGenerator.writeEndObject();
  }

  private void writeAddress(JsonGenerator jsonGenerator, Address address) throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("street1", address.getStreet1());
    jsonGenerator.writeStringField("street2", address.getStreet2());
    jsonGenerator.writeStringField("zipCode", address.getZipCode());
    jsonGenerator.writeStringField("city", address.getCity());
    jsonGenerator.writeStringField("state", address.getState());

    jsonGenerator.writeEndObject();
  }

  //  public DataModel<Product, Warehouse, Employee, Carrier> deserializeFromJSONModel(
  //      String filePath, ObjectMapper objectMapper) throws IOException {
  //    LOG.info("Data model deserializing from file: {} to Java Objects", filePath);
  //
  //    Stopwatch stopwatch = new Stopwatch().start();
  //    List<Product> products = null;
  //    List<Warehouse> warehouses = null;
  //    List<Employee> employees = null;
  //    List<Carrier> carriers = null;
  //    Stats stats = null;
  //
  //    Map<String, District> districtMap = new HashMap<>();
  //
  //    try (InputStream inputStream = new FileInputStream(filePath);
  //        // ObjectMapper objectMapper = new ObjectMapper();
  //        // objectMapper.registerModule(new JavaTimeModule());
  //        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  //        // JsonFactory jsonFactory = objectMapper.getFactory();
  //
  //        JsonParser jsonParser = objectMapper.getFactory().createParser(inputStream)) {
  //
  //      // Read the start of the JSON object
  //      if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
  //        throw new IOException("Invalid JSON file format. Expected the start of an object.");
  //      }
  //
  //      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
  //        String fieldName = jsonParser.getCurrentName();
  //
  //        // Read the "products" array
  //        if ("products".equals(fieldName)) {
  //          jsonParser.nextToken();
  //          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
  //            Product product = objectMapper.readValue(jsonParser, Product.class);
  //            products.add(product);
  //          }
  //        }
  //
  //        // Read the "warehouses" array
  //        else if ("warehouses".equals(fieldName)) {
  //          jsonParser.nextToken();
  //          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
  //            Warehouse warehouse = objectMapper.readValue(jsonParser, Warehouse.class);
  //            warehouses.add(warehouse);
  //            mapDistrictByIdModel(districtMap, warehouse);
  //          }
  //        }
  //
  //        // Central idea:
  //        // Employee objects will be read and instantiated to close the circular reference in
  // Java
  //        // objects
  //        // Read the "employees" array
  //        else if ("employees".equals(fieldName)) {
  //          jsonParser.nextToken();
  //          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
  //            Employee employee = readEmployeeModel(jsonParser, districtMap, warehouses);
  //            employees.add(employee);
  //          }
  //        }
  //
  //        // Read the "carriers" array
  //        else if ("carriers".equals(fieldName)) {
  //          jsonParser.nextToken();
  //          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
  //            Carrier carrier = objectMapper.readValue(jsonParser, Carrier.class);
  //            carriers.add(carrier);
  //          }
  //        }
  //
  //        // Read the "stats" object
  //        else if ("stats".equals(fieldName)) {
  //          jsonParser.nextToken();
  //          stats = objectMapper.readValue(jsonParser, Stats.class);
  //        }
  //      }
  //      jsonParser.close();
  //    }
  //
  //    stopwatch.stop();
  //    LOG.info("Deserialization took : {}", stopwatch.getDuration());
  //
  //    return new DataGeneratorModel(products, warehouses, employees, carriers, stats);
  //  }

  // TODO: Dokumentation
  public Stats deserializeStatsFromJSON(String filePath) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();

    try (InputStream inputStream = new FileInputStream(filePath);
        JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

      return readStats(jsonParser);
    }
  }

  private Stats readStats(JsonParser jsonParser) throws IOException {
    Stats stats = new Stats();

    if (jsonParser != null && jsonParser.nextToken() == JsonToken.START_OBJECT) {
      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
        if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
          String property = jsonParser.getCurrentName();
          jsonParser.nextToken(); // Move to the value token

          setStatsProperty(stats, property, jsonParser);
        }
      }
    }

    return stats;
  }

  private void setStatsProperty(Stats stats, String property, JsonParser jsonParser)
      throws IOException {
    if (jsonParser == null) {
      throw new IllegalArgumentException("jsonParser cannot be null");
    }
    switch (property) {
      case "totalModelObjectCount":
        stats.setTotalModelObjectCount(jsonParser.getIntValue());
        break;
      case "durationMillis":
        stats.setDurationMillis(jsonParser.getLongValue());
        break;
      case "duration":
        stats.setDuration(jsonParser.getValueAsString());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  // TODO: Dokumentation
  public List<Product> deserializeProductsFromJSON(String filePath) throws IOException {
    List<Product> products = new ArrayList<>();

    JsonFactory jsonFactory = new JsonFactory();
    try (InputStream inputStream = new FileInputStream(filePath);
        JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = jsonParser.getCurrentName();

        // Find the "products" field in the JSON object
        if ("products".equals(fieldName)) {
          jsonParser.nextToken(); // Move to the start of the array

          // Deserialize each product in the array
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Product product = readProduct(jsonParser);
            products.add(product);
          }

          break; // Found the "products" field, exit the loop
        }
      }
    }

    return products;
  }

  // TODO: Dokumentation
  public List<Warehouse> deserializeWarehousesFromJSON(String filePath) throws IOException {
    List<Warehouse> warehouses = new ArrayList<>();

    JsonFactory jsonFactory = new JsonFactory();
    try (InputStream inputStream = new FileInputStream(filePath);
        JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = jsonParser.getCurrentName();

        // Find the "warehouses" field in the JSON object
        if ("warehouses".equals(fieldName)) {
          jsonParser.nextToken(); // Move to the start of the warehouses array
          warehouses = readWarehouses(jsonParser);
          break; // Found the "warehouses" field, exit the loop
        }
      }
    }

    return warehouses;
  }

  // TODO: Dokumentation
  public List<Employee> deserializeEmployeesFromJSON(String filePath) throws IOException {
    List<Employee> employees = new ArrayList<>();

    JsonFactory jsonFactory = new JsonFactory();

    try (InputStream inputStream = new FileInputStream(filePath);
        JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = jsonParser.getCurrentName();

        // Find the "employees" field in the JSON object
        if ("employees".equals(fieldName)) {
          jsonParser.nextToken(); // Move to the start of the employees array
          employees = readEmployees(jsonParser);
          break; // Found the "employees" field, exit the loop
        }
      }
    }

    return employees;
  }

  private List<Employee> readEmployees(JsonParser jsonParser) throws IOException {
    List<Employee> employees = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
        Employee employee = readEmployee(jsonParser);
        employees.add(employee);
      }
    }

    return employees;
  }

  private Employee readEmployee(JsonParser jsonParser) throws IOException {
    Employee employee = new Employee();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setEmployeeProperty(employee, property, jsonParser);
      }
    }

    return employee;
  }

  // TODO: Dokumentation
  public List<Carrier> deserializeCarriersFromJSON(String filePath) throws IOException {
    List<Carrier> carriers = new ArrayList<>();

    JsonFactory jsonFactory = new JsonFactory();

    try (InputStream inputStream = new FileInputStream(filePath);
        JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = jsonParser.getCurrentName();

        // Find the "carriers" field in the JSON object
        if ("carriers".equals(fieldName)) {
          jsonParser.nextToken(); // Move to the start of the carriers array
          carriers = readCarriers(jsonParser);
          break; // Found the "carriers" field, exit the loop
        }
      }
    }

    return carriers;
  }

  private List<Carrier> readCarriers(JsonParser jsonParser) throws IOException {
    List<Carrier> carriers = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
        Carrier carrier = readCarrier(jsonParser);
        carriers.add(carrier);
      }
    }

    return carriers;
  }

  private Carrier readCarrier(JsonParser jsonParser) throws IOException {
    Carrier carrier = new Carrier();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setCarrierProperty(carrier, property, jsonParser);
      }
    }

    return carrier;
  }

  private void setCarrierProperty(Carrier carrier, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        carrier.setId(jsonParser.getValueAsString());
        break;
      case "name":
        carrier.setName(jsonParser.getValueAsString());
        break;
      case "phoneNumber":
        carrier.setPhoneNumber(jsonParser.getValueAsString());
        break;
      case "address":
        // Deserialize the address object
        Address address = readAddress(jsonParser);
        carrier.setAddress(address);
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private void setEmployeeProperty(Employee employee, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        employee.setId(jsonParser.getValueAsString());
        break;
      case "firstName":
        employee.setFirstName(jsonParser.getValueAsString());
        break;
      case "middleName":
        employee.setMiddleName(jsonParser.getValueAsString());
        break;
      case "lastName":
        employee.setLastName(jsonParser.getValueAsString());
        break;
      case "address":
        // Deserialize the address object
        Address address = readAddress(jsonParser);
        employee.setAddress(address);
        break;
      case "phoneNumber":
        employee.setPhoneNumber(jsonParser.getValueAsString());
        break;
      case "email":
        employee.setEmail(jsonParser.getValueAsString());
        break;
      case "title":
        employee.setTitle(jsonParser.getValueAsString());
        break;
      case "username":
        employee.setUsername(jsonParser.getValueAsString());
        break;
      case "password":
        employee.setPassword(jsonParser.getValueAsString());
        break;
      case "role":
        employee.setRole(jsonParser.getValueAsString());
        break;
      case "district":
        for (Map.Entry<String, District> districtEntry : districtMap.entrySet()) {
          if (districtEntry.getKey().equals(jsonParser.getValueAsString())) {
            employee.setDistrict(districtEntry.getValue());
            break;
          }
        }
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private List<Warehouse> readWarehouses(JsonParser jsonParser) throws IOException {
    List<Warehouse> warehouses = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
        Warehouse warehouse = readWarehouse(jsonParser);
        closeCircularReferencesInWarehouse(warehouse);
        warehouses.add(warehouse);
      }
    }

    return warehouses;
  }

  // TODO: Dokumentation
  private void closeCircularReferencesInWarehouse(Warehouse warehouse) {
    // referencing of stock objects to warehouse
    for (Stock stock : warehouse.getStocks()) {
      stock.setWarehouse(warehouse);
    }

    // referencing of districts to warehouse
    for (District district : warehouse.getDistricts()) {
      district.setWarehouse(warehouse);

      for (Customer customer : district.getCustomers()) {
        // referencing from customer to district
        customer.setDistrict(district);
        // referencing from customer's payment to customer and district
        for (Payment payment : customer.getPayments()) {
          payment.setCustomer(customer);
          payment.setDistrict(district);
        }
      }

      // referencing from order to district
      for (Order order : district.getOrders()) {
        order.setDistrict(district);

        // referencing from orderItem to order and supplyingWarehouse
        for (OrderItem orderItem : order.getItems()) {
          orderItem.setOrder(order);
          orderItem.setSupplyingWarehouse(warehouse);
        }
      }
    }
  }

  private Warehouse readWarehouse(JsonParser jsonParser) throws IOException {
    Warehouse warehouse = new Warehouse();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setWarehouseProperty(warehouse, property, jsonParser);
      }
    }

    return warehouse;
  }

  private void setWarehouseProperty(Warehouse warehouse, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        warehouse.setId(jsonParser.getValueAsString());
        break;
      case "name":
        warehouse.setName(jsonParser.getValueAsString());
        break;
      case "address":
        Address address = readAddress(jsonParser);
        warehouse.setAddress(address);
        break;
      case "districts":
        List<District> districts = readDistricts(jsonParser);
        warehouse.setDistricts(districts);
        break;
      case "stocks":
        List<Stock> stocks = readStocks(jsonParser);
        warehouse.setStocks(stocks);
        break;
      case "salesTax":
        warehouse.setSalesTax(jsonParser.getValueAsDouble());
        break;
      case "yearToDateBalance":
        warehouse.setYearToDateBalance(jsonParser.getValueAsDouble());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private List<Stock> readStocks(JsonParser jsonParser) throws IOException {
    List<Stock> stocks = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
        Stock stock = readStock(jsonParser);
        stocks.add(stock);
      }
    }

    return stocks;
  }

  private Stock readStock(JsonParser jsonParser) throws IOException {
    Stock stock = new Stock();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setStockProperty(stock, property, jsonParser);
      }
    }

    return stock;
  }

  private void setStockProperty(Stock stock, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        stock.setId(jsonParser.getValueAsString());
        break;
      case "product":
        Product product = readProduct(jsonParser);
        stock.setProduct(product);
        break;
      case "quantity":
        stock.setQuantity(jsonParser.getValueAsInt());
        break;
        // TODO: set reference later
      case "warehouse":
        jsonParser.skipChildren();
        break;
      case "yearToDateBalance":
        stock.setYearToDateBalance(jsonParser.getValueAsDouble());
        break;
      case "orderCount":
        stock.setOrderCount(jsonParser.getValueAsInt());
        break;
      case "remoteCount":
        stock.setRemoteCount(jsonParser.getValueAsInt());
        break;
      case "data":
        stock.setData(jsonParser.getValueAsString());
        break;
      case "dist01":
        stock.setDist01(jsonParser.getValueAsString());
        break;
      case "dist02":
        stock.setDist02(jsonParser.getValueAsString());
        break;
      case "dist03":
        stock.setDist03(jsonParser.getValueAsString());
        break;
      case "dist04":
        stock.setDist04(jsonParser.getValueAsString());
        break;
      case "dist05":
        stock.setDist05(jsonParser.getValueAsString());
        break;
      case "dist06":
        stock.setDist06(jsonParser.getValueAsString());
        break;
      case "dist07":
        stock.setDist07(jsonParser.getValueAsString());
        break;
      case "dist08":
        stock.setDist08(jsonParser.getValueAsString());
        break;
      case "dist09":
        stock.setDist09(jsonParser.getValueAsString());
        break;
      case "dist10":
        stock.setDist10(jsonParser.getValueAsString());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private void setDistrictProperty(District district, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        district.setId(jsonParser.getValueAsString());
        break;
      case "warehouse":
        // TODO: set reference later
        // we will add the reference by iterating over the warehouse's districts
        jsonParser.skipChildren();
        break;
        // Muss mal anschauen, ob man die Customer-Liste nicht irgendwie besser hineingeben kann
      case "customers":
        customers = readCustomers(jsonParser);
        district.setCustomers(customers);
        break;
      case "orders":
        List<Order> orders = readOrders(jsonParser, customers);
        district.setOrders(orders);
        break;
      case "name":
        district.setName(jsonParser.getValueAsString());
        break;
      case "address":
        Address address = readAddress(jsonParser);
        district.setAddress(address);
        break;
      case "salesTax":
        district.setSalesTax(jsonParser.getValueAsDouble());
        break;
      case "yearToDateBalance":
        district.setYearToDateBalance(jsonParser.getValueAsDouble());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private List<Customer> readCustomers(JsonParser jsonParser) throws IOException {
    List<Customer> customers = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
        Customer customer = readCustomer(jsonParser);
        customers.add(customer);
      }
    }

    return customers;
  }

  private Customer readCustomer(JsonParser jsonParser) throws IOException {
    Customer customer = new Customer();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setCustomerProperty(customer, property, jsonParser);
      }
    }

    return customer;
  }

  private void setCustomerProperty(Customer customer, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        customer.setId(jsonParser.getValueAsString());
        break;
      case "firstName":
        customer.setFirstName(jsonParser.getValueAsString());
        break;
      case "middleName":
        customer.setMiddleName(jsonParser.getValueAsString());
        break;
      case "lastName":
        customer.setLastName(jsonParser.getValueAsString());
        break;
      case "address":
        Address address = readAddress(jsonParser);
        customer.setAddress(address);
        break;
      case "phoneNumber":
        customer.setPhoneNumber(jsonParser.getValueAsString());
        break;
      case "email":
        customer.setEmail(jsonParser.getValueAsString());
        break;
      case "since":
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        customer.setSince(
            LocalDateTime.parse(jsonParser.getValueAsString().substring(0, 19), formatter));
        break;
      case "payments":
        List<Payment> payments = readPayments(jsonParser);
        customer.setPayments(payments);
        break;
        // TODO: set reference later , aber hier ist eigentlich immer null
      case "orders":
        jsonParser.skipChildren();
        break;
      case "credit":
        customer.setCredit(jsonParser.getValueAsString());
        break;
      case "creditLimit":
        customer.setCreditLimit(jsonParser.getValueAsDouble());
        break;
      case "discount":
        customer.setDiscount(jsonParser.getValueAsDouble());
        break;
      case "balance":
        customer.setBalance(jsonParser.getValueAsDouble());
        break;
      case "yearToDatePayment":
        customer.setYearToDatePayment(jsonParser.getValueAsDouble());
        break;
      case "paymentCount":
        customer.setPaymentCount(jsonParser.getValueAsInt());
        break;
      case "deliveryCount":
        customer.setDeliveryCount(jsonParser.getValueAsInt());
        break;
      case "data":
        customer.setData(jsonParser.getValueAsString());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private List<Payment> readPayments(JsonParser jsonParser) throws IOException {
    List<Payment> payments = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {

        Payment payment = readPayment(jsonParser);
        payments.add(payment);
      }
    }
    return payments;
  }

  private Payment readPayment(JsonParser jsonParser) throws IOException {
    Payment payment = new Payment();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setPaymentProperty(payment, property, jsonParser);
      }
    }

    return payment;
  }

  private void setPaymentProperty(Payment payment, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        payment.setId(jsonParser.getValueAsString());
        break;
        // TODO: set reference later
      case "customer":
        jsonParser.skipChildren();
        break;
      case "date":
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        payment.setDate(
            LocalDateTime.parse(jsonParser.getValueAsString().substring(0, 19), formatter));
        break;
        // TODO: set reference later
      case "district":
        jsonParser.skipChildren();
        break;
      case "amount":
        payment.setAmount(jsonParser.getValueAsDouble());
        break;
      case "data":
        payment.setData(jsonParser.getValueAsString());
        break;

      default:
        // Ignore unknown properties
        break;
    }
  }

  private List<Order> readOrders(JsonParser jsonParser, List<Customer> customers)
      throws IOException {
    List<Order> orders = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
        Order order = readOrder(jsonParser, customers);
        orders.add(order);
      }
    }

    return orders;
  }

  private Order readOrder(JsonParser jsonParser, List<Customer> customers) throws IOException {
    Order order = new Order();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setOrderProperty(order, property, jsonParser, customers);
      }
    }

    return order;
  }

  private void setOrderProperty(
      Order order, String property, JsonParser jsonParser, List<Customer> customers)
      throws IOException {
    switch (property) {
      case "id":
        order.setId(jsonParser.getValueAsString());
        break;
        // TODO: set reference later
      case "district":
        jsonParser.skipChildren();
        break;
      case "customer":
        for (Customer customer : customers) {
          if (Objects.equals(customer.getId(), jsonParser.getValueAsString())) {
            order.setCustomer(customer);
            break;
          }
        }
        break;
      case "entryDate":
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        order.setEntryDate(
            LocalDateTime.parse(jsonParser.getValueAsString().substring(0, 19), formatter));
        break;
        // TODO: set reference later, aber ist eigentlich null
      case "carrier":
        String nextFieldName = jsonParser.nextFieldName();
        if (Objects.equals(nextFieldName, "items")) {
          order.setCarrier(null);
        } else {
          String field_Id = jsonParser.getCurrentName();
          Carrier carrier = readCarrierInOrder(jsonParser, field_Id);
          order.setCarrier(carrier);
          break;
        }
      case "items":
        List<OrderItem> orderItems = readOrderItems(jsonParser);
        order.setItems(orderItems);
        break;
      case "itemCount":
        order.setItemCount(jsonParser.getIntValue());
        break;
      case "allLocal":
        order.setAllLocal(jsonParser.getBooleanValue());
        break;
      case "fulfilled":
        order.setFulfilled(jsonParser.getBooleanValue());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private Carrier readCarrierInOrder(JsonParser jsonParser, String field_Id) throws IOException {
    Carrier carrier = new Carrier();
    jsonParser.nextToken();
    setCarrierProperty(carrier, field_Id, jsonParser);

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setCarrierProperty(carrier, property, jsonParser);
      }
    }

    return carrier;
  }

  private List<OrderItem> readOrderItems(JsonParser jsonParser) throws IOException {
    List<OrderItem> orderItems = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {

        OrderItem orderItem = readOrderItem(jsonParser);
        orderItems.add(orderItem);
      }
    }

    return orderItems;
  }

  private OrderItem readOrderItem(JsonParser jsonParser) throws IOException {
    OrderItem orderItem = new OrderItem();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setOrderItemProperty(orderItem, property, jsonParser);
      }
    }

    return orderItem;
  }

  private void setOrderItemProperty(OrderItem orderItem, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        orderItem.setId(jsonParser.getValueAsString());
        break;
        // TODO: set reference later
      case "order":
        jsonParser.skipChildren();
        break;
      case "number":
        orderItem.setNumber(jsonParser.getValueAsInt());
        break;
      case "product":
        Product product = readProduct(jsonParser);
        orderItem.setProduct(product);
        break;
        // TODO: set reference later
      case "supplyingWarehouse":
        jsonParser.skipChildren();
        break;
      case "deliveryDate":
        if (Objects.equals(jsonParser.getValueAsString(), null)) {
          orderItem.setDeliveryDate(null);
        } else {
          DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
          orderItem.setDeliveryDate(
              LocalDateTime.parse(jsonParser.getValueAsString().substring(0, 19), formatter));
        }
        break;
      case "quantity":
        orderItem.setQuantity(jsonParser.getValueAsInt());
        break;
      case "amount":
        orderItem.setAmount(jsonParser.getValueAsDouble());
        break;
      case "distInfo":
        orderItem.setDistInfo(jsonParser.getValueAsString());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private List<District> readDistricts(JsonParser jsonParser) throws IOException {
    List<District> districts = new ArrayList<>();

    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
      if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {

        District district = readDistrict(jsonParser);
        districts.add(district);
        districtMap.put(district.getId(), district);
      }
    }

    return districts;
  }

  private District readDistrict(JsonParser jsonParser) throws IOException {
    District district = new District();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setDistrictProperty(district, property, jsonParser);
      }
    }

    return district;
  }

  private Address readAddress(JsonParser jsonParser) throws IOException {
    Address address = new Address();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setAddressProperty(address, property, jsonParser);
      }
    }

    return address;
  }

  private void setAddressProperty(Address address, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "street1":
        address.setStreet1(jsonParser.getValueAsString());
        break;
      case "street2":
        address.setStreet2(jsonParser.getValueAsString());
        break;
      case "zipCode":
        address.setZipCode(jsonParser.getValueAsString());
        break;
      case "city":
        address.setCity(jsonParser.getValueAsString());
        break;
      case "state":
        address.setState(jsonParser.getValueAsString());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  private Product readProduct(JsonParser jsonParser) throws IOException {
    Product product = new Product();

    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
        String property = jsonParser.getCurrentName();
        jsonParser.nextToken(); // Move to the value token

        setProductProperty(product, property, jsonParser);
      }
    }

    return product;
  }

  private void setProductProperty(Product product, String property, JsonParser jsonParser)
      throws IOException {
    switch (property) {
      case "id":
        product.setId(jsonParser.getValueAsString());
        break;
      case "imagePath":
        product.setImagePath(jsonParser.getValueAsString());
        break;
      case "name":
        product.setName(jsonParser.getValueAsString());
        break;
      case "price":
        product.setPrice(jsonParser.getValueAsDouble());
        break;
      case "data":
        product.setData(jsonParser.getValueAsString());
        break;
      default:
        // Ignore unknown properties
        break;
    }
  }

  // End of methods which are used in Streaming APIs parse JSON data incrementally

  // When using whole Model
  public static District fetchDistrictByIdModel(String districtID, List<Warehouse> warehouses) {
    for (Warehouse warehouse : warehouses) {
      for (int k = 0; k < warehouse.getDistricts().size(); k++)
        if (Objects.equals(districtID, warehouse.getDistricts().get(k).getId())) {
          return warehouse.getDistricts().get(k);
        }
    }
    return null;
  }

  private void mapDistrictByIdModel(Map<String, District> districtMap, Warehouse warehouse) {
    List<District> districtsList = warehouse.getDistricts();
    for (District district : districtsList) {
      districtMap.put(district.getId(), district);
    }
  }

  private Employee readEmployeeModel(
      JsonParser jsonParser, Map<String, District> districtMap, List<Warehouse> warehouses)
      throws IOException {
    Employee employee = new Employee();
    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      String property = jsonParser.getCurrentName();
      jsonParser.nextToken(); // Move to the value token

      if ("id".equals(property)) {
        employee.setId(jsonParser.getText());
      } else if ("firstName".equals(property)) {
        employee.setFirstName(jsonParser.getText());
      } else if ("middleName".equals(property)) {
        employee.setMiddleName(jsonParser.getText());
      } else if ("lastName".equals(property)) {
        employee.setLastName(jsonParser.getText());
      } else if ("address".equals(property)) {
        Address address = readAddressModel(jsonParser);
        employee.setAddress(address);
      } else if ("phoneNumber".equals(property)) {
        employee.setPhoneNumber(jsonParser.getText());
      } else if ("email".equals(property)) {
        employee.setEmail(jsonParser.getText());
      } else if ("title".equals(property)) {
        employee.setTitle(jsonParser.getText());
      } else if ("username".equals(property)) {
        employee.setUsername(jsonParser.getText());
      } else if ("password".equals(property)) {
        employee.setPassword(jsonParser.getText());
      } else if ("role".equals(property)) {
        employee.setRole(jsonParser.getText());
      } else if ("district".equals(property)) {
        String districtID = jsonParser.getText();
        District district = districtMap.get(districtID);

        if (district == null) {
          district = fetchDistrictByIdModel(districtID, warehouses);
          districtMap.put(districtID, district);
        }

        employee.setDistrict(district);
      }
    }

    return employee;
  }

  private Address readAddressModel(JsonParser jsonParser) throws IOException {
    Address address = new Address();
    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
      String addressProperty = jsonParser.getCurrentName();
      jsonParser.nextToken(); // Move to the value token

      if ("street1".equals(addressProperty)) {
        address.setStreet1(jsonParser.getText());
      } else if ("street2".equals(addressProperty)) {
        address.setStreet2(jsonParser.getText());
      } else if ("zipCode".equals(addressProperty)) {
        address.setZipCode(jsonParser.getText());
      } else if ("city".equals(addressProperty)) {
        address.setCity(jsonParser.getText());
      } else if ("state".equals(addressProperty)) {
        address.setState(jsonParser.getText());
      }
    }

    return address;
  }
}

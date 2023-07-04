package de.uniba.dsg.wss.api.controllers;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.representations.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Solved with the help of : #1
 * https://stackoverflow.com/questions/28821715/java-lang-classcastexception-java-util-linkedhashmap-cannot-be-cast-to-com-test
 * #2
 * https://stackoverflow.com/questions/15580010/error-deserializing-read-only-property-with-jackson
 * #3 https://gist.github.com/thurloat/2510887 #4
 * https://stackoverflow.com/questions/36182553/how-to-de-serialize-a-setter-less-property-using-jackson-in-java
 * #5
 * https://howtodoinjava.com/jackson/jackson-custom-serializer-deserializer/#4-custom-deserialization
 * #6 REDISSON CAN INCLUDE OBJECT REFERENCES ->
 * https://stackoverflow.com/questions/12279117/can-jedis-get-set-an-java-pojo/12355876#12355876 &&
 * ANOTHER LINK FOR REDISSON DESERIALIZATION :
 * https://github.com/redisson/redisson/wiki/4.-data-serialization #7 However there is a possibility
 * to store also object with help of jOhm:
 * https://stackoverflow.com/questions/12279117/can-jedis-get-set-an-java-pojo/12355876#12355876
 * https://github.com/xetorthio/johm
 * https://stackoverflow.com/questions/9584504/how-to-use-java-object-as-a-value-in-redis
 */
@RestController
public class RedisResourceController implements ResourceController {

  private final ProductRepository productRepository;
  private final EmployeeRepository employeeRepository;
  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final StockRepository stockRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CarrierRepository carrierRepository;
  private final ModelMapper modelMapper;

  public RedisResourceController(
      ProductRepository productRepository,
      EmployeeRepository employeeRepository,
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      StockRepository stockRepository,
      CustomerRepository customerRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      CarrierRepository carrierRepository) {
    this.productRepository = productRepository;
    this.employeeRepository = employeeRepository;
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.stockRepository = stockRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.carrierRepository = carrierRepository;
    modelMapper = new ModelMapper();
  }

  @Override
  public ResponseEntity<Iterable<ProductRepresentation>> getProducts() {
    return ResponseEntity.ok(
        productRepository.getAllProducts().entrySet().stream()
            .parallel()
            .map(p -> modelMapper.map(p.getValue(), ProductRepresentation.class))
            .collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    EmployeeData employee = employeeRepository.findEmployeeByName(username);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public ResponseEntity<List<WarehouseRepresentation>> getWarehouses() {
    return ResponseEntity.ok(
        warehouseRepository.getWarehouses().values().stream()
            .map(warehouseData -> modelMapper.map(warehouseData, WarehouseRepresentation.class))
            .collect(Collectors.toList()));
  }

  // WITH MODELMAPPER TODO: TESTE ES AUS MIT GETTER/SETTER , DA CUSTOMDESERIALIZER FUNKTIONIERT
  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    WarehouseData warehouse = warehouseRepository.getWarehouses().get(warehouseId);

    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse, WarehouseRepresentation.class);
    List<String> districtIds = warehouse.getDistrictRefsIds();

    List<DistrictRepresentation> districtRepresentations = new ArrayList<>();

    for (String districtId : districtIds) {
      DistrictData district = districtRepository.findById(districtId);

      DistrictRepresentation districtRepresentation =
          modelMapper.map(district, DistrictRepresentation.class);
      districtRepresentation.setWarehouse(warehouseRepresentation);
      districtRepresentations.add(districtRepresentation);
    }

    return ResponseEntity.ok(districtRepresentations);
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    WarehouseData warehouse = warehouseRepository.getWarehouses().get(warehouseId);

    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse, WarehouseRepresentation.class);
    List<String> stockIds = warehouse.getStockRefsIds();

    List<StockRepresentation> stockRepresentations = new ArrayList<>();

    for (String stockId : stockIds) {

      StockData stock = stockRepository.findById(stockId);
      ProductData product = productRepository.findById(stock.getProductRefId());

      StockRepresentation stockRepresentation = modelMapper.map(stock, StockRepresentation.class);
      ProductRepresentation productRepresentation =
          modelMapper.map(product, ProductRepresentation.class);

      stockRepresentation.setProduct(productRepresentation);
      stockRepresentation.setWarehouse(warehouseRepresentation);
      stockRepresentations.add(stockRepresentation);
    }

    return ResponseEntity.ok(stockRepresentations);
  }

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
    WarehouseData warehouse = warehouseRepository.getWarehouses().get(warehouseId);

    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    DistrictData district = districtRepository.findById(districtId);

    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    //    for(String distId : warehouse.getDistrictRefsIds()){
    //        if(!Objects.equals(distId, districtId)){
    //          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    //        }
    //    }

    DistrictRepresentation districtRepresentation =
        modelMapper.map(district, DistrictRepresentation.class);
    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse, WarehouseRepresentation.class);
    districtRepresentation.setWarehouse(warehouseRepresentation);

    List<CustomerRepresentation> customerRepresentations = new ArrayList<>();

    for (String customerId : district.getCustomerRefsIds()) {
      CustomerData customer = customerRepository.findById(customerId);

      CustomerRepresentation customerRepresentation =
          modelMapper.map(customer, CustomerRepresentation.class);
      customerRepresentation.setDistrict(districtRepresentation);
      customerRepresentations.add(customerRepresentation);
    }

    return ResponseEntity.ok(customerRepresentations);
  }

  /*TODO: It might be possible to reduce the calls to the redis server by deserializing products and carrier direct like address.
    It is unknown how it will impact the througput and latency by multiple + concurrent calls to the redis server;
    At the moment it is interesting also to understand how improve/reduce the calls, since we need to reconstruct the objects reference and circularity
    by our own.
  */
  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
    WarehouseData warehouse = warehouseRepository.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    DistrictData district = districtRepository.findById(districtId);

    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    //    for(String distId : warehouse.getDistrictRefsIds()){
    //      if(Objects.equals(distId, districtId)){
    //        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    //      }
    //    }

    DistrictRepresentation districtRepresentation =
        modelMapper.map(district, DistrictRepresentation.class);
    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse, WarehouseRepresentation.class);
    districtRepresentation.setWarehouse(warehouseRepresentation);

    List<OrderRepresentation> orderRepresentations = new ArrayList<>();

    // TODO: ANSTATT EINE HASHMAP würde auch Eine list für getOrderRefsIds() auch in Ordnung sein
    for (String orderId : district.getOrderRefsIds()) {
      // String orderId = entry.getKey();
      OrderData order = orderRepository.findById(orderId);
      OrderRepresentation orderRepresentation = modelMapper.map(order, OrderRepresentation.class);

      CarrierData carrier = carrierRepository.findById(order.getCarrierRefId());
      CarrierRepresentation carrierRepresentation =
          modelMapper.map(carrier, CarrierRepresentation.class);

      CustomerData customer = customerRepository.findById(order.getCustomerRefId());
      CustomerRepresentation customerRepresentation =
          modelMapper.map(customer, CustomerRepresentation.class);
      customerRepresentation.setDistrict(districtRepresentation);

      orderRepresentation.setCarrier(carrierRepresentation);
      orderRepresentation.setCustomer(customerRepresentation);
      orderRepresentation.setDistrict(districtRepresentation);

      List<OrderItemRepresentation> orderItemRepresentations = new ArrayList<>();

      for (String itemId : order.getItemsIds()) {
        OrderItemData orderItem = orderItemRepository.findById(itemId);
        OrderItemRepresentation orderItemRepresentation =
            modelMapper.map(orderItem, OrderItemRepresentation.class);

        ProductData product = productRepository.findById(orderItem.getProductRefId());
        ProductRepresentation productRepresentation =
            modelMapper.map(product, ProductRepresentation.class);

        orderItemRepresentation.setProduct(productRepresentation);
        orderItemRepresentation.setSupplyingWarehouse(warehouseRepresentation);

        orderItemRepresentations.add(orderItemRepresentation);
      }

      orderRepresentation.setItems(orderItemRepresentations);

      for (OrderItemRepresentation orderItemRepresentation : orderRepresentation.getItems()) {
        orderItemRepresentation.setOrder(orderRepresentation);
      }

      orderRepresentations.add(orderRepresentation);
      //      if(order.getItemsIds().size() == orderRepresentation.getItems().size()){
      //        break;
      //      }
    }

    return ResponseEntity.ok(orderRepresentations);
  }

  @Override
  public ResponseEntity<List<CarrierRepresentation>> getCarriers() {
    return ResponseEntity.ok(
        carrierRepository.getCarriers().entrySet().parallelStream()
            .map(c -> modelMapper.map(c.getValue(), CarrierRepresentation.class))
            .collect(Collectors.toList()));
  }
}

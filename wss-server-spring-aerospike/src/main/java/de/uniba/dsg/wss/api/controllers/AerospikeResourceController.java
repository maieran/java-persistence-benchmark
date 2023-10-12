package de.uniba.dsg.wss.api.controllers;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.representations.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AerospikeResourceController implements ResourceController {

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

  public AerospikeResourceController(
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
        StreamSupport.stream(productRepository.findAll().spliterator(), true)
            .map(p -> modelMapper.map(p, ProductRepresentation.class))
            .collect(Collectors.toList()));
  }

  /*  @Override
  public ResponseEntity<Iterable<ProductRepresentation>> getProducts() {
    return ResponseEntity.ok(
            productRepository.getAllProducts().entrySet().stream()
                    .parallel()
                    .map(p -> modelMapper.map(p.getValue(), ProductRepresentation.class))
                    .collect(Collectors.toList()));
  }*/

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    EmployeeData employee = employeeRepository.findEmployeeDataByUsername(username);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public ResponseEntity<List<WarehouseRepresentation>> getWarehouses() {
    return ResponseEntity.ok(
        StreamSupport.stream(warehouseRepository.findAll().spliterator(), true)
            .map(warehouseData -> modelMapper.map(warehouseData, WarehouseRepresentation.class))
            .collect(Collectors.toList()));
  }

  /*  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    Optional<WarehouseData> warehouseDataOptional =
        warehouseRepository.findById(Integer.parseInt(warehouseId));
    if (warehouseDataOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseData warehouse = warehouseDataOptional.get();
    List<DistrictRepresentation> districtRepresentations =
        warehouse.getDistricts().entrySet().parallelStream()
            .map(d -> modelMapper.map(d.getValue(), DistrictRepresentation.class))
            .collect(Collectors.toList());

    return ResponseEntity.ok(districtRepresentations);
  }*/

  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    Optional<WarehouseData> warehouse = warehouseRepository.findById(warehouseId);

    if (warehouse.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse.get(), WarehouseRepresentation.class);

    List<DistrictRepresentation> districtRepresentations =
        // BATCH CALL
        // TODO: implement Batch getDistrictsFromWarehouse
        districtRepository.getDistrictsFromWarehouse(warehouse.get().getDistrictRefsIds()).stream()
            .map(
                district -> {
                  DistrictRepresentation districtRepresentation =
                      modelMapper.map(district, DistrictRepresentation.class);
                  districtRepresentation.setWarehouse(warehouseRepresentation);
                  return districtRepresentation;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(districtRepresentations);
  }

  /*  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    Optional<WarehouseData> warehouseDataOptional = warehouseRepository.findById(warehouseId);
    if (warehouseDataOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseData warehouse = warehouseDataOptional.get();
    List<StockRepresentation> stockRepresentations =
        warehouse.getStocks().parallelStream()
            .map(s -> modelMapper.map(s, StockRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(stockRepresentations);
  }*/

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    Optional<WarehouseData> warehouse = warehouseRepository.findById(warehouseId);

    if (warehouse.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse.get(), WarehouseRepresentation.class);
    List<String> stockIds = warehouse.get().getStockRefsIds();
    // BATCH CALL
    // TODO: Implement Batch getStocksByWarehouse
    List<StockData> stocks = stockRepository.getStocksByWarehouse(stockIds);
    List<String> productIds =
        stocks.stream().map(StockData::getProductRefId).collect(Collectors.toList());

    // BATCH CALL
    // TODO: Implement Batch getProductsFromStocks
    Map<String, ProductData> products = productRepository.getProductsFromStocks(productIds);

    List<StockRepresentation> stockRepresentations =
        stocks.stream()
            .map(
                stock -> {
                  ProductData product = products.get(stock.getProductRefId());

                  StockRepresentation stockRepresentation =
                      modelMapper.map(stock, StockRepresentation.class);
                  ProductRepresentation productRepresentation =
                      modelMapper.map(product, ProductRepresentation.class);

                  stockRepresentation.setProduct(productRepresentation);
                  stockRepresentation.setWarehouse(warehouseRepresentation);
                  return stockRepresentation;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(stockRepresentations);
  }

  /*  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
    Optional<WarehouseData> warehouseDataOptional =
        warehouseRepository.findById(warehouseId);
    if (warehouseDataOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseData warehouse = warehouseDataOptional.get();

    DistrictData district = warehouse.getDistricts().get(districtId);
    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<CustomerRepresentation> customerRepresentations =
        district.getCustomers().parallelStream()
            .map(c -> modelMapper.map(c, CustomerRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(customerRepresentations);
  }*/

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
    Optional<WarehouseData> warehouse = warehouseRepository.findById(warehouseId);

    if (warehouse.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    Optional<DistrictData> district = districtRepository.findById(districtId);

    if (district.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    DistrictRepresentation districtRepresentation =
        modelMapper.map(district.get(), DistrictRepresentation.class);
    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse.get(), WarehouseRepresentation.class);
    districtRepresentation.setWarehouse(warehouseRepresentation);

    List<CustomerRepresentation> customerRepresentations =
        // BATCH CALL
        // TODO: Implement BATCH getCustomersByDistricts
        customerRepository.getCustomersByDistricts(district.get().getCustomerRefsIds()).stream()
            .map(
                customer -> {
                  CustomerRepresentation customerRepresentation =
                      modelMapper.map(customer, CustomerRepresentation.class);
                  customerRepresentation.setDistrict(districtRepresentation);
                  return customerRepresentation;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok(customerRepresentations);
  }

  /*  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
    Optional<WarehouseData> warehouseDataOptional =
        warehouseRepository.findById(warehouseId);

    if (warehouseDataOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    WarehouseData warehouse = warehouseDataOptional.get();

    DistrictData district = warehouse.getDistricts().get(districtId);
    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<OrderRepresentation> orderRepresentations =
        district.getOrders().entrySet().parallelStream()
            .map(o -> modelMapper.map(o.getValue(), OrderRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(orderRepresentations);
  }*/

  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
    Optional<WarehouseData> warehouse = warehouseRepository.findById(warehouseId);
    if (warehouse.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    Optional<DistrictData> district = districtRepository.findById(districtId);

    if (district.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    DistrictRepresentation districtRepresentation =
        modelMapper.map(district.get(), DistrictRepresentation.class);
    WarehouseRepresentation warehouseRepresentation =
        modelMapper.map(warehouse.get(), WarehouseRepresentation.class);
    districtRepresentation.setWarehouse(warehouseRepresentation);

    List<OrderRepresentation> orderRepresentations = new ArrayList<>();
    List<OrderItemRepresentation> orderItemRepresentations = new ArrayList<>();

    // BATCH CALL
    // TODO: Implement Batch getOrdersFromDistrict
    List<OrderData> orders =
        orderRepository.getOrdersFromDistrict(district.get().getOrderRefsIds());

    for (OrderData order : orders) {
      OrderRepresentation orderRepresentation = modelMapper.map(order, OrderRepresentation.class);

      Optional<CarrierData> carrier = carrierRepository.findById(order.getCarrierRefId());
      if (carrier.isPresent()) {
        CarrierRepresentation carrierRepresentation =
            modelMapper.map(carrier.get(), CarrierRepresentation.class);
        orderRepresentation.setCarrier(carrierRepresentation);
      } else {
        orderRepresentation.setCarrier(null);
      }

      Optional<CustomerData> customer = customerRepository.findById(order.getCustomerRefId());
      // TODO: Set ifPresent Check?!
      CustomerRepresentation customerRepresentation =
          modelMapper.map(customer.get(), CustomerRepresentation.class);
      customerRepresentation.setDistrict(districtRepresentation);

      // orderRepresentation.setCarrier(carrierRepresentation);
      orderRepresentation.setCustomer(customerRepresentation);
      orderRepresentation.setDistrict(districtRepresentation);

      // BATCH CALL
      // TODO: Implement Batch getOrderItemsByOrder
      List<OrderItemData> orderItems =
          orderItemRepository.getOrderItemsByOrder(order.getItemsIds());

      for (OrderItemData orderItem : orderItems) {
        OrderItemRepresentation orderItemRepresentation =
            modelMapper.map(orderItem, OrderItemRepresentation.class);

        Optional<ProductData> product = productRepository.findById(orderItem.getProductRefId());
        // TODO: Set ifPresent Check?!
        ProductRepresentation productRepresentation =
            modelMapper.map(product.get(), ProductRepresentation.class);

        orderItemRepresentation.setProduct(productRepresentation);
        orderItemRepresentation.setSupplyingWarehouse(warehouseRepresentation);

        orderItemRepresentations.add(orderItemRepresentation);
      }
      orderRepresentation.setItems(orderItemRepresentations);

      for (OrderItemRepresentation orderItemRepresentation : orderRepresentation.getItems()) {
        orderItemRepresentation.setOrder(orderRepresentation);
      }
      orderRepresentations.add(orderRepresentation);
    }
    return ResponseEntity.ok(orderRepresentations);
  }

  @Override
  public ResponseEntity<List<CarrierRepresentation>> getCarriers() {
    return ResponseEntity.ok(
        StreamSupport.stream(carrierRepository.findAll().spliterator(), true)
            .map(c -> modelMapper.map(c, CarrierRepresentation.class))
            .collect(Collectors.toList()));
  }
}

package de.uniba.dsg.wss.api.controllers;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.representations.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
                StreamSupport.stream(warehouseRepository.findAll().spliterator(), true)
                        .map(warehouseData -> modelMapper.map(warehouseData, WarehouseRepresentation.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
        Optional<WarehouseData> warehouseDataOptional = warehouseRepository.findById(Integer.parseInt(warehouseId));
        if (warehouseDataOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        WarehouseData warehouse = warehouseDataOptional.get();
        List<DistrictRepresentation> districtRepresentations =
                warehouse.getDistricts().entrySet().parallelStream()
                        .map(d -> modelMapper.map(d.getValue(), DistrictRepresentation.class))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(districtRepresentations);
    }

    @Override
    public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
        Optional<WarehouseData> warehouseDataOptional = warehouseRepository.findById(Integer.parseInt(warehouseId));
        if (warehouseDataOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        WarehouseData warehouse = warehouseDataOptional.get();
        List<StockRepresentation> stockRepresentations =
                warehouse.getStocks().parallelStream()
                        .map(s -> modelMapper.map(s, StockRepresentation.class))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(stockRepresentations);
    }

    @Override
    public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(String warehouseId, String districtId) {
        Optional<WarehouseData> warehouseDataOptional = warehouseRepository.findById(Integer.parseInt(warehouseId));
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

    }

    @Override
    public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(String warehouseId, String districtId) {
        Optional<WarehouseData> warehouseDataOptional = warehouseRepository.findById(Integer.parseInt(warehouseId));
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
    }

    @Override
    public ResponseEntity<List<CarrierRepresentation>> getCarriers() {
        return ResponseEntity.ok(
                        StreamSupport.stream(carrierRepository.findAll().spliterator(), true)
                        .map(c -> modelMapper.map(c, CarrierRepresentation.class))
                        .collect(Collectors.toList()));
    }
}

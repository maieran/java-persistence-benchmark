package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisDataWriter
    implements DataWriter<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private static final Logger LOG = LogManager.getLogger(RedisDataWriter.class);
  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;
  private final EmployeeRepository employeeRepository;
  private final StockRepository stockRepository;
  private final DistrictRepository districtRepository;
  private final PaymentRepository paymentRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CustomerRepository customerRepository;

  @Autowired
  public RedisDataWriter(
      ProductRepository productRepository,
      CarrierRepository carrierRepository,
      WarehouseRepository warehouseRepository,
      EmployeeRepository employeeRepository,
      StockRepository stockRepository,
      DistrictRepository districtRepository,
      PaymentRepository paymentRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      CustomerRepository customerRepository) {

    this.productRepository = productRepository;
    this.carrierRepository = carrierRepository;
    this.warehouseRepository = warehouseRepository;
    this.employeeRepository = employeeRepository;
    this.stockRepository = stockRepository;
    this.districtRepository = districtRepository;
    this.paymentRepository = paymentRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.customerRepository = customerRepository;
  }

  @Override
  public void write(DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> model) {
    Stopwatch stopwatch = new Stopwatch().start();
    if (!supports(model)) {
      throw new UnsupportedDataModelException("Data model was null");
    }

    RedisDataModel redisDataModel = (RedisDataModel) model;

    productRepository.saveAll(redisDataModel.getIdsToProducts());

    carrierRepository.saveAll(redisDataModel.getIdsToCarriers());

    warehouseRepository.saveAll(redisDataModel.getIdsToWarehouses());

    stockRepository.saveAll(redisDataModel.getIdsToStocks());

    districtRepository.saveAll(redisDataModel.getIdsToDistricts());

    employeeRepository.saveAll(redisDataModel.getIdsToEmployees());

    customerRepository.saveAll(redisDataModel.getIdsToCustomers());

    orderRepository.saveAll(redisDataModel.getIdsToOrders());

    orderItemRepository.saveAll(redisDataModel.getIdsToOrderItems());

    paymentRepository.saveAll(redisDataModel.getIdsToPayments());

    stopwatch.stop();
    LOG.info("Wrote model data to database, took {}", stopwatch.getDuration());
  }

  @Override
  public boolean supports(DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> model) {
    return DataWriter.super.supports(model);
  }
}

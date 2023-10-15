package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Writes the wholesale supplier model to Aerospike-based storage system via implemented interfaces.
 *
 * @author Andre Maier
 */
@Component
public class AerospikeDataWriter
    implements DataWriter<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private static final Logger LOG = LogManager.getLogger(AerospikeDataModel.class);
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
  public AerospikeDataWriter(
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

    AerospikeDataModel aerospikeDataModel = (AerospikeDataModel) model;

    productRepository.saveAll(aerospikeDataModel.getIdsToProducts());

    employeeRepository.saveAll(aerospikeDataModel.getIdsToEmployees());

    carrierRepository.saveAll(aerospikeDataModel.getIdsToCarriers());

    warehouseRepository.saveAll(aerospikeDataModel.getIdsToWarehouses());

    stockRepository.saveAll(aerospikeDataModel.getIdsToStocks());

    districtRepository.saveAll(aerospikeDataModel.getIdsToDistricts());

    customerRepository.saveAll(aerospikeDataModel.getIdsToCustomers());

    orderRepository.saveAll(aerospikeDataModel.getIdsToOrders());

    orderItemRepository.saveAll(aerospikeDataModel.getIdsToOrderItems());

    paymentRepository.saveAll(aerospikeDataModel.getIdsToPayments());

    stopwatch.stop();
    LOG.info("Wrote model data to database, took {}", stopwatch.getDuration());
  }

  @Override
  public boolean supports(DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> model) {
    return DataWriter.super.supports(model);
  }
}

package de.uniba.dsg.wss;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.gen.AerospikeDataConverter;
import de.uniba.dsg.wss.data.gen.AerospikeDataModel;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.service.TestAerospikeConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(TestAerospikeConfiguration.class)
public abstract class AerospikeTest {

  @Autowired protected ProductRepository productRepository;
  @Autowired protected CarrierRepository carrierRepository;
  @Autowired protected WarehouseRepository warehouseRepository;
  @Autowired protected EmployeeRepository employeeRepository;
  @Autowired protected StockRepository stockRepository;
  @Autowired protected DistrictRepository districtRepository;
  @Autowired protected PaymentRepository paymentRepository;
  @Autowired protected OrderRepository orderRepository;
  @Autowired protected OrderItemRepository orderItemRepository;
  @Autowired protected CustomerRepository customerRepository;

  public AerospikeTest() {}

  public void prepareTestStorage() {

    AerospikeDataModel dataModel =
        new AerospikeDataConverter().convert(new TestDataGenerator().generate());

    productRepository.saveAll(dataModel.getIdsToProducts());

    carrierRepository.saveAll(dataModel.getIdsToCarriers());

    warehouseRepository.saveAll(dataModel.getIdsToWarehouses());

    stockRepository.saveAll(dataModel.getIdsToStocks());

    districtRepository.saveAll(dataModel.getIdsToDistricts());

    employeeRepository.saveAll(dataModel.getIdsToEmployees());

    customerRepository.saveAll(dataModel.getIdsToCustomers());

    orderRepository.saveAll(dataModel.getIdsToOrders());

    orderItemRepository.saveAll(dataModel.getIdsToOrderItems());

    paymentRepository.saveAll(dataModel.getIdsToPayments());
  }
}

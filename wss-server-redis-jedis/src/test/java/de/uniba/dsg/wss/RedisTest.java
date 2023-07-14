package de.uniba.dsg.wss;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.gen.RedisDataConverter;
import de.uniba.dsg.wss.data.gen.RedisDataModel;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.service.TestRedisConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(TestRedisConfiguration.class)
public abstract class RedisTest {

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

  public RedisTest() {}

  public void prepareTestStorage() {

    RedisDataModel dataModel = new RedisDataConverter().convert(new TestDataGenerator().generate());

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

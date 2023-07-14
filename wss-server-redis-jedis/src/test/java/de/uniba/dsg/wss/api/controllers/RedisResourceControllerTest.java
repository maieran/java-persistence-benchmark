package de.uniba.dsg.wss.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import de.uniba.dsg.wss.RedisTest;
import de.uniba.dsg.wss.auth.Privileges;
import de.uniba.dsg.wss.data.transfer.representations.*;
import java.util.List;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

// @SpringBootTest(classes = {TestRedisConfiguration.class, RedisResourceController.class})
@SpringBootTest
public class RedisResourceControllerTest extends RedisTest {

  @Autowired private RedisResourceController controller;

  @BeforeEach
  public void setUp() {
    /*    RedisDataConverter converter = new RedisDataConverter();
    RedisDataModel model = converter.convert(new TestDataGenerator().generate());
    warehouseRepository.saveAll(model.getIdsToWarehouses());*/
    prepareTestStorage();
  }

  //  @AfterEach
  //  public void tearDown() {
  //    warehouseRepository.deleteAll();
  //  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkWarehouse() {
    ResponseEntity<List<WarehouseRepresentation>> warehouses = controller.getWarehouses();
    assertEquals(5, warehouses.getBody().size());
  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkWarehouseDistricts() {
    ResponseEntity<List<DistrictRepresentation>> districts = controller.getWarehouseDistricts("W0");
    assertEquals(2, districts.getBody().size());
    assertFalse(districts.getBody().stream().noneMatch(d -> "D0".equals(d.getId())));
  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkProducts() {
    ResponseEntity<Iterable<ProductRepresentation>> products = controller.getProducts();
    assertEquals(10, IterableUtil.sizeOf(products.getBody()));
  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkWarehouseStocks() {
    ResponseEntity<List<StockRepresentation>> stocks = controller.getWarehouseStocks("W0");
    assertEquals(5, stocks.getBody().size());
  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkDistrictCustomers() {
    ResponseEntity<List<CustomerRepresentation>> customers =
        controller.getDistrictCustomers("W0", "D0");
    assertEquals(2, customers.getBody().size());
  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkOrderPerDistrict() {
    ResponseEntity<List<OrderRepresentation>> orders = controller.getDistrictOrders("W0", "D0");
    assertEquals(2, orders.getBody().size());
  }

  @Test
  @WithMockUser(
      username = "terminal_user_1",
      authorities = {Privileges.READ_DATA_ALL})
  public void checkCarriers() {
    ResponseEntity<List<CarrierRepresentation>> carriers = controller.getCarriers();
    assertEquals(1, carriers.getBody().size());
  }
}

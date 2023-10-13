package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.wss.AerospikeTest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.services.AerospikeOrderStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AerospikeOrderStatusIntegrationTests extends AerospikeTest {

  @Autowired AerospikeOrderStatusService aerospikeOrderStatusService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    OrderStatusRequest request = new OrderStatusRequest("W0", "D0", null, null);

    assertThrows(IllegalStateException.class, () -> aerospikeOrderStatusService.process(request));
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    // most recent order is O10
    OrderStatusRequest request = new OrderStatusRequest("W0", "D0", "C0", null);

    OrderStatusResponse res = aerospikeOrderStatusService.process(request);

    assertEquals("W0", res.getWarehouseId());
    assertEquals("D0", res.getDistrictId());
    assertEquals("C0", res.getCustomerId());
    assertEquals("C0-first", res.getCustomerFirstName());
    assertEquals("C0-middle", res.getCustomerMiddleName());
    assertEquals("C0-last", res.getCustomerLastName());
    assertEquals(
        customerRepository.getCustomers().get("C0").getBalance(), res.getCustomerBalance());
    assertEquals("O10", res.getOrderId());
    assertEquals(1, res.getItemStatus().size());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerEmailIsProvided() {
    // most recent order is O10
    OrderStatusRequest request = new OrderStatusRequest("W0", "D0", null, "C0@jbp.io");

    OrderStatusResponse res = aerospikeOrderStatusService.process(request);

    assertEquals("W0", res.getWarehouseId());
    assertEquals("D0", res.getDistrictId());
    assertEquals("C0", res.getCustomerId());
    assertEquals("C0-first", res.getCustomerFirstName());
    assertEquals("C0-middle", res.getCustomerMiddleName());
    assertEquals("C0-last", res.getCustomerLastName());
    assertEquals(
        customerRepository.getCustomers().get("C0").getBalance(), res.getCustomerBalance());
    assertEquals("O10", res.getOrderId());
    assertEquals(1, res.getItemStatus().size());
  }
}

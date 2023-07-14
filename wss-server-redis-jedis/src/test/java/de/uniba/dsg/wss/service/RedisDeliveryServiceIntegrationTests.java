package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.*;

import de.uniba.dsg.wss.RedisTest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.services.RedisDeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisDeliveryServiceIntegrationTests extends RedisTest {

  @Autowired RedisDeliveryService redisDeliveryService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void deliveryProcessingReturnsExpectedValues() {
    DeliveryRequest request = new DeliveryRequest("W0", "CC0");
    DeliveryResponse response = redisDeliveryService.process(request);

    assertEquals(request.getWarehouseId(), response.getWarehouseId());
    assertEquals(request.getCarrierId(), response.getCarrierId());
  }

  @Test
  public void checkIfOldestOrderIsUpdated() {
    DeliveryRequest request = new DeliveryRequest("W0", "CC0");
    DeliveryResponse response = redisDeliveryService.process(request);

    assertEquals(
        carrierRepository.getCarriers().get("CC0").getId(),
        orderRepository.getOrders().get("O0").getCarrierRefId());
    assertNull(orderRepository.getOrders().get("O10").getCarrierRefId());

    assertTrue(orderRepository.getOrders().get("O0").isFulfilled());
    assertFalse(orderRepository.getOrders().get("O10").isFulfilled());
  }

  @Test
  public void checkIfCustomerUpdated() {
    DeliveryRequest request = new DeliveryRequest("W0", "CC0");
    DeliveryResponse response = redisDeliveryService.process(request);

    assertNotNull(
        orderItemRepository
            .findById(orderRepository.getOrders().get("O0").getItemsIds().get(0))
            .getDeliveryDate());
    assertEquals(
        orderItemRepository
            .findById(orderRepository.getOrders().get("O0").getItemsIds().get(0))
            .getAmount(),
        customerRepository.getCustomers().get("C0").getBalance());
  }
}

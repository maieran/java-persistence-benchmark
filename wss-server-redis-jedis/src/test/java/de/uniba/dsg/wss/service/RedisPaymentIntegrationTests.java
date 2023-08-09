package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uniba.dsg.wss.RedisTest;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisPaymentIntegrationTests extends RedisTest {

  @Autowired PaymentService paymentService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    PaymentRequest request = new PaymentRequest("W0", "D0", null, null, 0.9);
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalStateException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingFailsWithWrongId() {
    PaymentRequest request = new PaymentRequest("W0", "D0", "X0", null, 0.9);
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalStateException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingPersistsNewPayment() {
    String customerId = "C0";
    double amount = 12.45;
    int paymentCount = customerRepository.getCustomers().get(customerId).getPaymentCount();
    assertEquals(1, paymentCount);
    PaymentRequest request = new PaymentRequest("W0", "D0", customerId, null, amount);

    PaymentResponse res = paymentService.process(request);

    CustomerData customer = customerRepository.getCustomers().get(customerId);
    assertEquals(res.getPaymentAmount() * (-1), customer.getBalance());

    assertTrue(
        paymentRepository.getPayments().entrySet().stream()
            // .filter(entry -> entry.getValue() != null && entry.getKey() != null)
            .anyMatch(
                entry ->
                    entry.getValue().getId().equals(res.getPaymentId())
                        && customer.getPaymentRefsIds().contains(entry.getKey())));
    assertEquals(2, customer.getPaymentCount());
    assertEquals(2, customer.getPaymentRefsIds().size());
  }

  @Test
  public void processingUpdatesWarehouseAndDistrict() {
    String customerId = "C0";
    double amount = 12.45;
    CustomerData customer = customerRepository.getCustomers().get(customerId);
    int paymentCount = customerRepository.getCustomers().get(customerId).getPaymentCount();
    PaymentRequest request = new PaymentRequest("W0", "D0", customerId, null, amount);

    paymentService.process(request);

    WarehouseData warehouse = warehouseRepository.getWarehouses().get(request.getWarehouseId());
    assertEquals(request.getAmount(), warehouse.getYearToDateBalance());

    DistrictData district = districtRepository.getDistricts().get(request.getDistrictId());
    assertEquals(request.getAmount(), district.getYearToDateBalance());
  }
}

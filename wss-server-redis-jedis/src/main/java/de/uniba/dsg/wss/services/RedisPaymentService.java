package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.DistrictRepository;
import de.uniba.dsg.wss.data.access.PaymentRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.PaymentData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.service.PaymentService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisPaymentService extends PaymentService {

  private final WarehouseRepository warehouseRepository;
  private final CustomerRepository customerRepository;
  private final DistrictRepository districtRepository;
  private final PaymentRepository paymentRepository;

  @Autowired
  public RedisPaymentService(
      WarehouseRepository warehouseRepository,
      CustomerRepository customerRepository,
      DistrictRepository districtRepository,
      PaymentRepository paymentRepository) {
    this.warehouseRepository = warehouseRepository;
    this.customerRepository = customerRepository;
    this.districtRepository = districtRepository;
    this.paymentRepository = paymentRepository;
  }

  @Override
  public PaymentResponse process(PaymentRequest paymentRequest) {
    CustomerData customer;
    if (paymentRequest.getCustomerId() == null) {
      customer =
          customerRepository.getCustomers().entrySet().stream()
              .parallel()
              .filter(c -> c.getValue().getEmail().equals(paymentRequest.getCustomerEmail()))
              .findAny()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Failed to find customer with email "
                              + paymentRequest.getCustomerEmail()))
              .getValue();
    } else {
      customer = customerRepository.findById(paymentRequest.getCustomerId());
      if (customer == null) {
        throw new IllegalStateException(
            "Failed to find customer with email " + paymentRequest.getCustomerEmail());
      }
    }

    WarehouseData warehouse = warehouseRepository.findById(paymentRequest.getWarehouseId());
    DistrictData district = districtRepository.findById(paymentRequest.getDistrictId());

    // new payment creation
    PaymentData payment =
        new PaymentData(
            customer.getId(),
            LocalDateTime.now(),
            paymentRequest.getAmount(),
            buildPaymentData(warehouse.getName(), district.getName()));

    // update payment and dependent objects
    // copy the customer data is here important since there could be concurrent updates on the same
    // customer object...
    CustomerData copiedCustomer =
        storePaymentAndUpdateDependentObjects(warehouse, district, customer, payment);

    // response object
    PaymentResponse paymentResponse = new PaymentResponse(paymentRequest);
    paymentResponse.setPaymentId(payment.getId());
    paymentResponse.setCustomerId(copiedCustomer.getId());
    paymentResponse.setCustomerCredit(copiedCustomer.getCredit());
    paymentResponse.setCustomerCreditLimit(copiedCustomer.getCreditLimit());
    paymentResponse.setCustomerDiscount(copiedCustomer.getDiscount());
    paymentResponse.setCustomerBalance(copiedCustomer.getBalance());

    return paymentResponse;
  }

  // TODO: Need to check for concurrency yet, since redis promises to have a thread-safe client when
  // using jedispool
  private CustomerData storePaymentAndUpdateDependentObjects(
      WarehouseData warehouse, DistrictData district, CustomerData customer, PaymentData payment) {
    double amount = payment.getAmount();
    paymentRepository.save(payment);

    // update warehouse - increase year to balance
    warehouse.increaseYearToBalance(amount);

    warehouseRepository.save(warehouse);
    // update district - increase year to balance
    district.increaseYearToBalance(amount);

    districtRepository.save(district);

    CustomerData copiedCustomer;
    // Here we test without syncronized like in MS-Sync
    // add payment reference over its id to customer
    customer.getPaymentRefsIds().add(payment.getId());
    // update customer - decrease balance - req.amount
    customer.decreaseBalance(amount);
    // update customer - increase year to date balance + req.amount
    customer.increaseYearToBalance(amount);
    // update customer - update payment count + 1
    customer.increasePaymentCount();
    // update customer if he/she has bad credit
    if (customerHasBadCredit(customer.getCredit())) {
      customer.updateData(
          buildNewCustomerData(
              customer.getId(), warehouse.getId(), district.getId(), amount, customer.getData()));
    }

    copiedCustomer = new CustomerData(customer);
    customerRepository.save(copiedCustomer);
    return copiedCustomer;
  }
}

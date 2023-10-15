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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link PaymentService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 * @author Andre Maier
 */
@Service
public class AerospikePaymentService extends PaymentService {

  private final WarehouseRepository warehouseRepository;
  private final CustomerRepository customerRepository;
  private final DistrictRepository districtRepository;
  private final PaymentRepository paymentRepository;

  @Autowired
  public AerospikePaymentService(
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
    Optional<CustomerData> customer;
    if (paymentRequest.getCustomerId() == null) {
      customer =
          Optional.ofNullable(
              customerRepository.getCustomers().entrySet().stream()
                  .parallel()
                  .filter(c -> c.getValue().getEmail().equals(paymentRequest.getCustomerEmail()))
                  .findAny()
                  .orElseThrow(
                      () ->
                          new IllegalStateException(
                              "Failed to find customer with email "
                                  + paymentRequest.getCustomerEmail()))
                  .getValue());
    } else {
      customer = customerRepository.findById(paymentRequest.getCustomerId());
      if (customer.isEmpty()) {
        throw new IllegalStateException(
            "Failed to find customer with email " + paymentRequest.getCustomerEmail());
      }
    }

    Optional<WarehouseData> warehouse =
        warehouseRepository.findById(paymentRequest.getWarehouseId());
    Optional<DistrictData> district = districtRepository.findById(paymentRequest.getDistrictId());

    // new payment creation
    PaymentData payment =
        new PaymentData(
            customer.get().getId(),
            LocalDateTime.now(),
            paymentRequest.getAmount(),
            buildPaymentData(warehouse.get().getName(), district.get().getName()));

    // update payment and dependent objects
    // copy the customer data is here important since there could be concurrent updates on the same
    // customer object...
    CustomerData copiedCustomer =
        storePaymentAndUpdateDependentObjects(
            warehouse.get(), district.get(), customer.get(), payment);

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

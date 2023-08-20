package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerData extends PersonData {

  private final DistrictData districtRef;
  private final Map<String, OrderData> orderRefs;
  private final List<PaymentData> paymentRefs;

  private final LocalDateTime since;
  private final String credit;
  private final double creditLimit;
  private final double discount;

  private int deliveryCount;
  private String data;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;

  public CustomerData(
      String id,
      String firstName,
      String middleName,
      String lastName,
      AddressData addressData,
      String phoneNumber,
      String mail,
      DistrictData districtRef,
      LocalDateTime since,
      String credit,
      double creditLimit,
      double discount,
      double balance,
      double yearToDatePayment,
      int paymentCount,
      int deliveryCount,
      String data) {
    super(id, firstName, middleName, lastName, addressData, phoneNumber, mail);
    this.districtRef = districtRef;
    this.since = since;
    this.credit = credit;
    this.creditLimit = creditLimit;
    this.discount = discount;
    this.balance = balance;
    this.yearToDatePayment = yearToDatePayment;
    this.paymentCount = paymentCount;
    this.deliveryCount = deliveryCount;
    this.data = data;
    this.orderRefs = new ConcurrentHashMap<>();
    this.paymentRefs = new ArrayList<>();
  }

  public CustomerData(CustomerData customer) {
    super(
        customer.getId(),
        customer.getFirstName(),
        customer.getMiddleName(),
        customer.getLastName(),
        customer.getAddress(),
        customer.getPhoneNumber(),
        customer.getEmail());
    this.districtRef = customer.districtRef;
    this.since = customer.since;
    this.credit = customer.credit;
    this.creditLimit = customer.creditLimit;
    this.discount = customer.discount;
    this.balance = customer.balance;
    this.yearToDatePayment = customer.yearToDatePayment;
    this.paymentCount = customer.paymentCount;
    this.deliveryCount = customer.deliveryCount;
    this.data = customer.data;
    this.orderRefs = null;
    this.paymentRefs = null;
  }

  public DistrictData getDistrict() {
    return this.districtRef;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public String getCredit() {
    return credit;
  }

  public double getCreditLimit() {
    return creditLimit;
  }

  public double getDiscount() {
    return discount;
  }

  public Map<String, OrderData> getOrderRefs() {
    return this.orderRefs;
  }

  public List<PaymentData> getPaymentRefs() {
    return this.paymentRefs;
  }

  public void decreaseBalance(double amount) {

    balance -= amount;
  }

  public void increaseBalance(double amount) {

    balance += amount;
  }

  public double getBalance() {

    return balance;
  }

  public void increaseYearToBalance(double amount) {

    this.yearToDatePayment += amount;
  }

  public double getYearToDatePayment() {

    return yearToDatePayment;
  }

  public void increasePaymentCount() {

    this.paymentCount++;
  }

  public int getPaymentCount() {

    return paymentCount;
  }

  public void increaseDeliveryCount() {

    this.deliveryCount++;
  }

  public int getDeliveryCount() {

    return deliveryCount;
  }

  public void updateData(String buildNewCustomerData) {

    this.data = buildNewCustomerData;
  }

  public String getData() {

    return data;
  }
}

package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * A customer of the wholesale supplier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@Document(collection = "Customer")
public class CustomerData extends PersonData {

  // Reference via ID
  private String districtRefId;

  // Reference via ID
  private Map<String, String> orderRefsIds;

  // Reference via ID
  private List<String> paymentRefsIds;

  private final LocalDateTime since;

  private final String credit;
  private final double creditLimit;
  private final double discount;

  private int deliveryCount;
  private String data;
  private double balance;

  @Field("ytdPayment")
  private double yearToDatePayment;

  private int paymentCount;

  @PersistenceConstructor
  public CustomerData(
      String id,
      String firstName,
      String middleName,
      String lastName,
      AddressData address,
      String phoneNumber,
      String email,
      String districtRefId,
      LocalDateTime since,
      String credit,
      double creditLimit,
      double discount,
      double balance,
      double yearToDatePayment,
      int paymentCount,
      int deliveryCount,
      String data) {
    super(id, firstName, middleName, lastName, address, phoneNumber, email);
    this.districtRefId = districtRefId;
    this.since = since;
    this.credit = credit;
    this.creditLimit = creditLimit;
    this.discount = discount;
    this.balance = balance;
    this.yearToDatePayment = yearToDatePayment;
    this.paymentCount = paymentCount;
    this.deliveryCount = deliveryCount;
    this.data = data;
    this.orderRefsIds = new ConcurrentHashMap<>();
    this.paymentRefsIds = new ArrayList<>();
  }

  /**
   * Creates a shallow copy of the provided customer.
   *
   * @param customer a customer, must not be {@code null}
   */
  public CustomerData(CustomerData customer) {
    super(
        customer.getId(),
        customer.getFirstName(),
        customer.getMiddleName(),
        customer.getLastName(),
        customer.getAddress(),
        customer.getPhoneNumber(),
        customer.getEmail());
    this.districtRefId = customer.districtRefId;
    this.since = customer.since;
    this.credit = customer.credit;
    this.creditLimit = customer.creditLimit;
    this.discount = customer.discount;
    this.balance = customer.balance;
    this.yearToDatePayment = customer.yearToDatePayment;
    this.paymentCount = customer.paymentCount;
    this.deliveryCount = customer.deliveryCount;
    this.data = customer.data;
    this.orderRefsIds = customer.orderRefsIds;
    this.paymentRefsIds = customer.paymentRefsIds;
  }

  public String getDistrictRefId() {
    return districtRefId;
  }

  public void setDistrictRefId(String districtRefId) {
    this.districtRefId = districtRefId;
  }

  public Map<String, String> getOrderRefsIds() {
    return orderRefsIds;
  }

  public void setOrderRefsIds(Map<String, String> orderRefsIds) {
    this.orderRefsIds = orderRefsIds;
  }

  public List<String> getPaymentRefsIds() {
    return paymentRefsIds;
  }

  public void setPaymentRefsIds(List<String> paymentRefsIds) {
    this.paymentRefsIds = paymentRefsIds;
  }

  /*  public void setSince(LocalDateTime since){
    this.since = since;
  }*/

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

  public int getDeliveryCount() {
    return deliveryCount;
  }

  public void setDeliveryCount(int deliveryCount) {
    this.deliveryCount = deliveryCount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public double getYearToDatePayment() {
    return yearToDatePayment;
  }

  public void setYearToDatePayment(double yearToDatePayment) {
    this.yearToDatePayment = yearToDatePayment;
  }

  public int getPaymentCount() {
    return paymentCount;
  }

  public void setPaymentCount(int paymentCount) {
    this.paymentCount = paymentCount;
  }

  public void increaseDeliveryCount() {
    this.deliveryCount++;
  }

  public void increaseYearToBalance(double amount) {
    this.yearToDatePayment += amount;
  }

  public void decreaseBalance(double amount) {
    balance -= amount;
  }

  public void increasePaymentCount() {
    this.paymentCount++;
  }

  public void updateData(String buildNewCustomerData) {
    this.data = buildNewCustomerData;
  }
}

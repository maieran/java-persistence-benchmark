package de.uniba.dsg.wss.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Payment")
public class PaymentData extends BaseData implements Serializable {

  // Object reference
  // private CustomerData customerRef;

  // Reference via ID
  private String customerRefId;

  private LocalDateTime date;
  private double amount;
  private String data;

  public PaymentData(
      String id, String customerRefId, LocalDateTime date, double amount, String data) {
    super(id);
    this.customerRefId = customerRefId;
    this.date = date;
    this.amount = amount;
    this.data = data;
  }

  public String getCustomerRefId() {
    return customerRefId;
  }

  public void setCustomerRefId(String customerRefId) {
    this.customerRefId = customerRefId;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}

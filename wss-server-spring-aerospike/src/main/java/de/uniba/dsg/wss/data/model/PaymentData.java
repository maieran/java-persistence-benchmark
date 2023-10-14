package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.PersistenceConstructor;

@Document(collection = "Payment")
public class PaymentData extends BaseData {

  private String customerRefId;

  // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime date;

  private double amount;
  private String data;

  @PersistenceConstructor
  public PaymentData(
      String id, String customerRefId, LocalDateTime date, double amount, String data) {
    super(id);
    this.customerRefId = customerRefId;
    this.date = date;
    this.amount = amount;
    this.data = data;
  }

  public PaymentData(String customerRefId, LocalDateTime date, double amount, String data) {
    super();
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

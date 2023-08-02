package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.redis.core.RedisHash;

/**
 * A payment made by a {@link CustomerData customer}, when retrieved via their unique identifier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@RedisHash("Payment")
public class PaymentData extends BaseData implements Serializable {

  // Reference via ID
  private String customerRefId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime date;

  private double amount;
  private String data;

  @JsonCreator
  public PaymentData(
      @JsonProperty("id") String id,
      @JsonProperty("customerRefId") String customerRefId,
      @JsonProperty("date") LocalDateTime date,
      @JsonProperty("amount") double amount,
      @JsonProperty("data") String data) {
    super(id);
    this.customerRefId = customerRefId;
    this.date = date;
    this.amount = amount;
    this.data = data;
  }

  public PaymentData(
      @JsonProperty("customerRefId") String customerRefId,
      @JsonProperty("date") LocalDateTime date,
      @JsonProperty("amount") double amount,
      @JsonProperty("data") String data) {
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

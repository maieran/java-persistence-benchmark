package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.springframework.data.redis.core.RedisHash;

/**
 * The available amount of a specific {@link ProductData product} at some {@link WarehouseData
 * warehouse}, which can be retrieved via their unique identifier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@RedisHash("Stock")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class StockData extends BaseData implements Serializable {

  public static int increaseQuantity = 100;
  // reference via id
  private final String warehouseRefId;
  // reference via id
  private final String productRefId;
  private int quantity;
  private double yearToDateBalance;
  private int orderCount;
  private int remoteCount;
  private final String data;
  private final String dist01;
  private final String dist02;
  private final String dist03;
  private final String dist04;
  private final String dist05;
  private final String dist06;
  private final String dist07;
  private final String dist08;
  private final String dist09;
  private final String dist10;

  @JsonCreator
  public StockData(
      @JsonProperty("id") String id,
      @JsonProperty("warehouseRefId") String warehouseRefId,
      @JsonProperty("productRefId") String productRefId,
      @JsonProperty("quantity") int quantity,
      @JsonProperty("yearToDateBalance") double yearToDateBalance,
      @JsonProperty("orderCount") int orderCount,
      @JsonProperty("remoteCount") int remoteCount,
      @JsonProperty("data") String data,
      @JsonProperty("dist01") String dist01,
      @JsonProperty("dist02") String dist02,
      @JsonProperty("dist03") String dist03,
      @JsonProperty("dist04") String dist04,
      @JsonProperty("dist05") String dist05,
      @JsonProperty("dist06") String dist06,
      @JsonProperty("dist07") String dist07,
      @JsonProperty("dist08") String dist08,
      @JsonProperty("dist09") String dist09,
      @JsonProperty("dist10") String dist10) {
    super(id);
    this.warehouseRefId = warehouseRefId;
    this.productRefId = productRefId;
    this.quantity = quantity;
    this.yearToDateBalance = yearToDateBalance;
    this.orderCount = orderCount;
    this.remoteCount = remoteCount;
    this.data = data;
    this.dist01 = dist01;
    this.dist02 = dist02;
    this.dist03 = dist03;
    this.dist04 = dist04;
    this.dist05 = dist05;
    this.dist06 = dist06;
    this.dist07 = dist07;
    this.dist08 = dist08;
    this.dist09 = dist09;
    this.dist10 = dist10;
  }

  public String getWarehouseRefId() {
    return this.warehouseRefId;
  }

  public String getProductRefId() {
    return this.productRefId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    this.orderCount = orderCount;
  }

  public int getRemoteCount() {
    return remoteCount;
  }

  public void setRemoteCount(int remoteCount) {
    this.remoteCount = remoteCount;
  }

  public String getData() {
    return data;
  }

  public String getDist01() {
    return dist01;
  }

  public String getDist02() {
    return dist02;
  }

  public String getDist03() {
    return dist03;
  }

  public String getDist04() {
    return dist04;
  }

  public String getDist05() {
    return dist05;
  }

  public String getDist06() {
    return dist06;
  }

  public String getDist07() {
    return dist07;
  }

  public String getDist08() {
    return dist08;
  }

  public String getDist09() {
    return dist09;
  }

  public String getDist10() {
    return dist10;
  }

  public boolean reduceQuantity(int quantity) {
    if (this.quantity < quantity) {
      this.quantity += increaseQuantity;
      return false;
    }
    this.quantity -= quantity;
    this.yearToDateBalance += quantity;
    this.orderCount++;
    return true;
  }
}

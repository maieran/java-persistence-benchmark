package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("OrderItem")
public class OrderItemData extends BaseData implements Serializable {

  // Object reference
  // private final OrderData orderRef;

  // Reference via ID
  private String orderRefId;

  // Object reference
  // private final ProductData productRef;

  // Reference via ID
  private String productRefId;

  // Object reference
  // private final WarehouseData supplyingWarehouseRef;

  // Reference via ID
  private String supplyingWarehouseRefId;

  private int number;
  private int quantity;
  private int leftQuantityInStock;
  private String distInfo;
  private double amount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime deliveryDate;

  @JsonCreator
  public OrderItemData(
      @JsonProperty("id") String id,
      @JsonProperty("orderRefId") String orderRefId,
      @JsonProperty("productRefId") String productRefId,
      @JsonProperty("supplyingWarehouseRefId") String supplyingWarehouseRefId,
      @JsonProperty("number") int number,
      @JsonProperty("deliveryDate") LocalDateTime deliveryDate,
      @JsonProperty("quantity") int quantity,
      @JsonProperty("leftQuantityInStock") int leftQuantityInStock,
      @JsonProperty("amount") double amount,
      @JsonProperty("distInfo") String distInfo) {
    super(id);
    this.orderRefId = orderRefId;
    this.productRefId = productRefId;
    this.supplyingWarehouseRefId = supplyingWarehouseRefId;
    this.number = number;
    this.deliveryDate = deliveryDate;
    this.quantity = quantity;
    this.leftQuantityInStock = leftQuantityInStock;
    this.amount = amount;
    this.distInfo = distInfo;
  }

  public OrderItemData(
      @JsonProperty("orderRefId") String orderRefId,
      @JsonProperty("productRefId") String productRefId,
      @JsonProperty("supplyingWarehouseRefId") String supplyingWarehouseRefId,
      @JsonProperty("number") int number,
      @JsonProperty("quantity") int quantity,
      @JsonProperty("leftQuantityInStock") int leftQuantityInStock,
      @JsonProperty("amount") double amount,
      @JsonProperty("distInfo") String distInfo) {
    super();
    this.orderRefId = orderRefId;
    this.productRefId = productRefId;
    this.supplyingWarehouseRefId = supplyingWarehouseRefId;
    this.number = number;
    // this.deliveryDate = null;
    this.quantity = quantity;
    this.leftQuantityInStock = leftQuantityInStock;
    this.amount = amount;
    this.distInfo = distInfo;
  }

  public String getOrderRefId() {
    return orderRefId;
  }

  public void setOrderRefId(String orderRefId) {
    this.orderRefId = orderRefId;
  }

  public String getProductRefId() {
    return productRefId;
  }

  public void setProductRefId(String productRefId) {
    this.productRefId = productRefId;
  }

  public String getSupplyingWarehouseRefId() {
    return supplyingWarehouseRefId;
  }

  public void setSupplyingWarehouseRefId(String supplyingWarehouseRefId) {
    this.supplyingWarehouseRefId = supplyingWarehouseRefId;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getLeftQuantityInStock() {
    return leftQuantityInStock;
  }

  public void setLeftQuantityInStock(int leftQuantityInStock) {
    this.leftQuantityInStock = leftQuantityInStock;
  }

  public String getDistInfo() {
    return distInfo;
  }

  public void setDistInfo(String distInfo) {
    this.distInfo = distInfo;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public void updateDeliveryDate() {
    this.deliveryDate = LocalDateTime.now();
  }
}

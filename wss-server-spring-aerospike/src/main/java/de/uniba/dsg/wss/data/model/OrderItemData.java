package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;

@Document(collection = "OrderItem")
public class OrderItemData extends BaseData {

  // Reference via ID
  private String orderRefId;
  // Reference via ID
  private String productRefId;
  // Reference via ID
  @Field("supplWareRefId")
  private String supplyingWarehouseRefId;

  private int number;
  private int quantity;

  @Field("lftQtyInStck")
  private int leftQuantityInStock;

  private String distInfo;
  private double amount;

  // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime deliveryDate;

  public OrderItemData(
      String id,
      String orderRef,
      String productRef,
      String supplyingWarehouseRef,
      int number,
      LocalDateTime deliveryDate,
      int quantity,
      int leftQuantityInStock,
      double amount,
      String distInfo) {
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
      String orderRef,
      String productRef,
      String supplyingWarehouseRef,
      int number,
      LocalDateTime deliveryDate,
      int quantity,
      int leftQuantityInStock,
      double amount,
      String distInfo) {
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

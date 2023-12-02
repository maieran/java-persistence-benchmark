package de.uniba.dsg.wss.data.model;

import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;

/**
 * The available amount of a specific {@link ProductData product} at some {@link WarehouseData
 * warehouse}, which can be retrieved via their unique identifier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@Document(collection = "Stock")
public class StockData extends BaseData {

  public static int increaseQuantity = 100;
  // reference via id
  private final String warehouseRefId;
  // reference via id
  private final String productRefId;
  private int quantity;

  @Field("ytdBalance")
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

  public StockData(
      String id,
      String warehouseRefId,
      String productRefId,
      int quantity,
      double yearToDateBalance,
      int orderCount,
      int remoteCount,
      String data,
      String dist01,
      String dist02,
      String dist03,
      String dist04,
      String dist05,
      String dist06,
      String dist07,
      String dist08,
      String dist09,
      String dist10) {
    // optimization
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

  public void undoReduceQuantityOperation(int quantity) {
    this.quantity += quantity;
    this.yearToDateBalance -= quantity;
    this.orderCount--;
  }
}

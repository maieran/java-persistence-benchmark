package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.aerospike.mapping.Document;

@Document(collection = "Order")
public class OrderData extends BaseData {

  // Reference via ID
  private String districtRefId;
  // Reference via ID
  private String customerRefId;
  // Reference via ID
  private String carrierRefId;

  private LocalDateTime entryDate;

  private int itemCount;
  private final boolean allLocal;
  private boolean fulfilled;
  // Reference via Ids

  private List<String> itemsIds;

  public OrderData(
      String id,
      String districtRefId,
      String customerRefId,
      String carrierRefId,
      LocalDateTime entryDate,
      int itemCount,
      boolean allLocal,
      boolean fulfilled) {
    super(id);
    this.districtRefId = districtRefId;
    this.customerRefId = customerRefId;
    this.carrierRefId = carrierRefId;
    this.entryDate = entryDate;
    this.itemCount = itemCount;
    this.allLocal = allLocal;
    this.fulfilled = fulfilled;
    this.itemsIds = new ArrayList<>();
  }

  public OrderData(
      String districtRefId,
      String customerRefId,
      LocalDateTime entryDate,
      int itemCount,
      boolean allLocal) {
    super();
    this.districtRefId = districtRefId;
    this.customerRefId = customerRefId;
    this.entryDate = entryDate;
    this.itemCount = itemCount;
    this.allLocal = allLocal;
    this.fulfilled = false;
    this.itemsIds = new ArrayList<>();
  }

  public void setDistrictRefId(String districtRefId) {
    this.districtRefId = districtRefId;
  }

  public String getDistrictRefId() {
    return districtRefId;
  }

  public void setCustomerRefId(String customerRefId) {
    this.customerRefId = customerRefId;
  }

  public String getCustomerRefId() {
    return customerRefId;
  }

  public String getCarrierRefId() {
    return carrierRefId;
  }

  public void setCarrierRefId(String carrierRefId) {
    this.carrierRefId = carrierRefId;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    this.entryDate = entryDate;
  }

  public void setItemCount(int itemCount) {
    this.itemCount = itemCount;
  }

  public int getItemCount() {
    return itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public boolean isFulfilled() {
    return fulfilled;
  }

  public void setFulfilled(boolean fulfilled) {
    this.fulfilled = fulfilled;
  }

  public List<String> getItemsIds() {
    return itemsIds;
  }

  public void setItemsIds(List<String> itemsIds) {
    this.itemsIds = itemsIds;
  }

  public void setAsFulFilled() {
    this.fulfilled = true;
  }
}

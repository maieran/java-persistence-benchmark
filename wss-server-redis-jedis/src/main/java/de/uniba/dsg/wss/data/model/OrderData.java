package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.RedisHash;

/**
 * An order issued by a {@link CustomerData customer} for a certain amount of {@link ProductData
 * products}, when retrieved via their unique identifier.
 *
 * @see OrderItemData
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@RedisHash("Order")
public class OrderData extends BaseData implements Serializable {

  // Reference via ID
  private String districtRefId;
  // Reference via ID
  private String customerRefId;
  // Reference via ID
  private String carrierRefId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime entryDate;

  private int itemCount;
  private final boolean allLocal;
  private boolean fulfilled;
  // Reference via Ids
  @JsonProperty("itemsIds")
  private List<String> itemsIds;

  @JsonCreator
  public OrderData(
      @JsonProperty("id") String id,
      @JsonProperty("districtRefId") String districtRefId,
      @JsonProperty("customerRefId") String customerRefId,
      @JsonProperty("carrierRefId") String carrierRefId,
      @JsonProperty("entryDate") LocalDateTime entryDate,
      @JsonProperty("itemCount") int itemCount,
      @JsonProperty("allLocal") boolean allLocal,
      @JsonProperty("fulfilled") boolean fulfilled) {
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
      @JsonProperty("districtRefId") String districtRefId,
      @JsonProperty("customerRefId") String customerRefId,
      @JsonProperty("entryDate") LocalDateTime entryDate,
      @JsonProperty("itemCount") int itemCount,
      @JsonProperty("allLocal") boolean allLocal) {
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

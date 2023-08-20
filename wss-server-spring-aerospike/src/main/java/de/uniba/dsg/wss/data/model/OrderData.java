package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderData extends BaseData implements Comparable<OrderData> {

  private final DistrictData districtRef;
  private final CustomerData customerRef;
  private CarrierData carrierRef;

  private final LocalDateTime entryDate;
  private final int itemCount;
  private final boolean allLocal;
  private boolean fulfilled;

  private final List<OrderItemData> items;

  public OrderData(
      DistrictData districtRef,
      CustomerData customerRef,
      LocalDateTime entryDate,
      int itemCount,
      boolean allLocal) {
    super();
    this.districtRef = districtRef;
    this.customerRef = customerRef;
    this.entryDate = entryDate;
    this.itemCount = itemCount;
    this.allLocal = allLocal;
    this.fulfilled = false;
    this.items = new ArrayList<>();
  }

  public DistrictData getDistrictRef() {
    return districtRef;
  }

  public CustomerData getCustomerRef() {
    return customerRef;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public int getItemCount() {
    return itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public List<OrderItemData> getItems() {
    return items;
  }

  public CarrierData getCarrierRef() {
    return carrierRef;
  }

  public void updateCarrier(CarrierData carrier) {

    this.carrierRef = carrier;
  }

  public void setAsFulfilled() {

    this.fulfilled = true;
  }

  public boolean isNotFulfilled() {

    return !fulfilled;
  }

  public boolean isFulfilled() {

    return fulfilled;
  }

  @Override
  public int compareTo(OrderData o) {
    return this.entryDate.compareTo(o.entryDate);
  }
}

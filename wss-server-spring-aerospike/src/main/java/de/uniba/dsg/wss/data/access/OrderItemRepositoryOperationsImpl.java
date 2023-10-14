package de.uniba.dsg.wss.data.access;

import com.aerospike.client.*;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.OrderItemData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class OrderItemRepositoryOperationsImpl implements OrderItemRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  private final AerospikeClient aerospikeClient;

  @Autowired
  public OrderItemRepositoryOperationsImpl(
      AerospikeTemplate aerospikeTemplate, AerospikeClient aerospikeClient) {
    this.aerospikeTemplate = aerospikeTemplate;
    this.aerospikeClient = aerospikeClient;
  }

  @Override
  public void saveAll(Map<String, OrderItemData> idsToOrders) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToOrders.forEach((id, orderItem) -> aerospikeTemplate.save(orderItem));
  }

  @Override
  public List<OrderItemData> getOrderItemsByOrder(List<String> itemsIds) {
    List<OrderItemData> orderItems = new ArrayList<>();
    // 1.Step - Collect the keys/ids necessary to retrieve the objects
    Key[] keys = new Key[itemsIds.size()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(OrderItemData.class),
              itemsIds.get(i));
    }

    // 2.Step - Retrieve orderItemData from Aerospike data model
    Record[] records = aerospikeTemplate.getAerospikeClient().get(null, keys);

    // 3.Step - Populate the list of orderItems
    for (int i = 0; i < records.length; i++) {
      Record record = records[i];
      if (record != null) {

        // Create the OrderData instance
        OrderItemData orderItem =
            new OrderItemData(
                itemsIds.get(i), // Set the id using the itemsIds list
                record.getString("orderRefId"),
                record.getString("productRefId"),
                record.getString("supplWareRefId"),
                record.getInt("number"),
                convertEntryDate(record.getLong("deliveryDate")),
                record.getInt("quantity"),
                record.getInt("lftQtyInStck"),
                record.getDouble("amount"),
                record.getString("distInfo"));

        orderItems.add(orderItem);
      }
    }

    return orderItems.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  private LocalDateTime convertEntryDate(Long entryDateMillis) {
    if (entryDateMillis != null) {
      Instant instant = Instant.ofEpochMilli(entryDateMillis);
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    return null;
  }

  /*  @Override
  public List<OrderItemData> getOrderItemsByOrder(List<String> itemsIds) {
    List<OrderItemData> orderItems = new ArrayList<>();

    for (String id : itemsIds) {
      // Read the record for the key
      OrderItemData orderItem = aerospikeTemplate.findById(id, OrderItemData.class);

      // Check if the record exists
      if (orderItem != null) {
        orderItems.add(orderItem);
      }
    }

    return orderItems;
  }*/

  @Override
  public void storeUpdatedOrderItem(OrderItemData orderItem) {
    aerospikeTemplate.update(orderItem);
  }
  // TODO: HOW TO BATCH WRITE -  saveOrderItemsInBatch - Is this also Batch write?!
  @Override
  public void saveOrderItemsInBatch(List<OrderItemData> orderItemsList) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    for (OrderItemData orderItem : orderItemsList) {
      Key key =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(OrderItemData.class),
              orderItem.getId());

      Bin[] bins =
          new Bin[] {
            new Bin("id", orderItem.getId()),
            new Bin("orderRefId", orderItem.getOrderRefId()),
            new Bin("productRefId", orderItem.getProductRefId()),
            new Bin("supplWareRefId", orderItem.getSupplyingWarehouseRefId()),
            new Bin("number", orderItem.getNumber()),
            new Bin("quantity", orderItem.getQuantity()),
            new Bin("lftQtyInStck", orderItem.getLeftQuantityInStock()),
            new Bin("distInfo", orderItem.getDistInfo()),
            new Bin("amount", orderItem.getAmount())
          };

      aerospikeTemplate.getAerospikeClient().put(writePolicy, key, bins);
    }
  }

  // TODO: Do a proper a batch read ...
  @Override
  public Map<String, OrderItemData> getOrderItemsByIds(List<String> itemsIds) {
    Map<String, OrderItemData> orderItemsMap = new HashMap<>();

    aerospikeTemplate
        .findAll(OrderItemData.class)
        .filter(orderItem -> itemsIds.contains(orderItem.getId()))
        .forEach(orderItem -> orderItemsMap.put(orderItem.getId(), orderItem));

    return orderItemsMap;
  }
}

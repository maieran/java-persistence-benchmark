package de.uniba.dsg.wss.data.access;

import com.aerospike.client.*;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.OrderItemData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link OrderItemRepositoryOperations} interface
 * for accessing and modifying {@link OrderItemData orderItems}.
 *
 * @author Andre Maier
 */
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
    BatchPolicy batchPolicy = new BatchPolicy();
    int timeout_socket = 1800000;
    int timeout_read = 1800000;
    batchPolicy.setTimeouts(timeout_socket, timeout_read);

    Key[] keys =
        itemsIds.stream()
            .map(
                id ->
                    new Key(
                        aerospikeTemplate.getNamespace(),
                        aerospikeTemplate.getSetName(OrderItemData.class),
                        id))
            .toArray(Key[]::new);

    Record[] records = aerospikeTemplate.getAerospikeClient().get(batchPolicy, keys);

    List<OrderItemData> orderItems =
        IntStream.range(0, records.length)
            .filter(i -> records[i] != null)
            .mapToObj(
                i -> {
                  Record record = records[i];
                  String itemId = itemsIds.get(i);
                  return new OrderItemData(
                      itemId,
                      record.getString("orderRefId"),
                      record.getString("productRefId"),
                      record.getString("supplWareRefId"),
                      record.getInt("number"),
                      convertEntryDate(record.getLong("deliveryDate")),
                      record.getInt("quantity"),
                      record.getInt("lftQtyInStck"),
                      record.getDouble("amount"),
                      record.getString("distInfo"));
                })
            .collect(Collectors.toList());

    return orderItems;
    // return orderItems.stream().filter(Objects::nonNull).collect(Collectors.toList());
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

  /**
   * There is no direct collective batch operation, but performed as a single operation in a loop,
   * see in
   * 'https://github.com/aerospike/aerospike-client-java/blob/master/examples/src/com/aerospike/examples/BatchOperate.java'
   *
   * <p>Alternatively, aerospike vendors attempt to masquerade the write-batch operation via
   * "client.operate(..)" as a batch-write example, see under
   * 'https://docs.aerospike.com/server/guide/batch#example-batch-readwrite-operations'
   *
   * <p>source :
   * https://github.com/aerospike/aerospike-client-java/blob/master/examples/src/com/aerospike/examples/BatchOperate.java
   * Transaction operation : https://docs.aerospike.com/server/guide/transactions
   */
  @Override
  public void saveOrderItemsInBatch(List<OrderItemData> orderItemsList) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;
    writePolicy.setTimeout(1800000);

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

  /*
  @Override
  public Map<String, OrderItemData> getOrderItemsByIds(List<String> itemsIds) {
    Map<String, OrderItemData> orderItemsMap = new HashMap<>();

    aerospikeTemplate
        .findAll(OrderItemData.class)
        .filter(orderItem -> itemsIds.contains(orderItem.getId()))
        .forEach(orderItem -> orderItemsMap.put(orderItem.getId(), orderItem));

    return orderItemsMap;
  }*/

  @Override
  public Map<String, OrderItemData> getOrderItemsByIds(List<String> itemsIds) {
    BatchPolicy batchPolicy = new BatchPolicy();
    int timeout_socket = 1800000;
    int timeout_read = 1800000;
    batchPolicy.setTimeouts(timeout_socket, timeout_read);

    Key[] keys =
        itemsIds.stream()
            .map(
                id ->
                    new Key(
                        aerospikeTemplate.getNamespace(),
                        aerospikeTemplate.getSetName(OrderItemData.class),
                        id))
            .toArray(Key[]::new);

    Record[] records = aerospikeTemplate.getAerospikeClient().get(batchPolicy, keys);

    Map<String, OrderItemData> orderItems =
        IntStream.range(0, records.length)
            .parallel()
            .filter(i -> records[i] != null)
            .boxed()
            .collect(
                Collectors.toConcurrentMap(
                    itemsIds::get,
                    i -> {
                      Record record = records[i];
                      return new OrderItemData(
                          itemsIds.get(i),
                          record.getString("orderRefId"),
                          record.getString("productRefId"),
                          record.getString("supplWareRefId"),
                          record.getInt("number"),
                          convertEntryDate(record.getLong("deliveryDate")),
                          record.getInt("quantity"),
                          record.getInt("lftQtyInStck"),
                          record.getDouble("amount"),
                          record.getString("distInfo"));
                    }));

    return orderItems;
  }
}

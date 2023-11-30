package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.OrderData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link OrderRepositoryOperations} interface for
 * accessing and modifying {@link OrderData orders}.
 *
 * @author Andre Maier
 */
public class OrderRepositoryOperationsImpl implements OrderRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public OrderRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, OrderData> idsToOrders) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToOrders.forEach((id, order) -> aerospikeTemplate.save(order));
  }

  @Override
  public List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds) {
    BatchPolicy batchPolicy = new BatchPolicy();
    int timeout_socket = 1800000;
    int timeout_read = 1800000;
    batchPolicy.setTimeouts(timeout_socket, timeout_read);

    Key[] keys =
        orderRefsIds.stream()
            .map(
                id ->
                    new Key(
                        aerospikeTemplate.getNamespace(),
                        aerospikeTemplate.getSetName(OrderData.class),
                        id))
            .toArray(Key[]::new);

    Record[] records = aerospikeTemplate.getAerospikeClient().get(batchPolicy, keys);

    List<OrderData> orders =
        IntStream.range(0, records.length)
            .parallel()
            .filter(i -> records[i] != null)
            .mapToObj(
                i -> {
                  Record record = records[i];

                  OrderData order =
                      new OrderData(
                          orderRefsIds.get(i),
                          record.getString("districtRefId"),
                          record.getString("customerRefId"),
                          record.getString("carrierRefId"),
                          convertEntryDate(record.getLong("entryDate")),
                          record.getInt("itemCount"),
                          record.getBoolean("allLocal"),
                          record.getBoolean("fulfilled"));

                  order.setItemsIds((List<String>) record.getList("itemsIds"));
                  return order;
                })
            .collect(Collectors.toList());

    return orders.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  private LocalDateTime convertEntryDate(Long entryDateMillis) {
    if (entryDateMillis != null) {
      Instant instant = Instant.ofEpochMilli(entryDateMillis);
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    return null;
  }

  /*
  @Override
  public List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds) {
    List<OrderData> orders = new ArrayList<>();

    for (String id : orderRefsIds) {
      // Read the record for the key
      OrderData order = aerospikeTemplate.findById(id, OrderData.class);

      // Check if the record exists
      if (order != null) {
        orders.add(order);
      }
    }

    return orders;
  }*/

  @Override
  public void storeUpdatedOrder(OrderData order) {
    aerospikeTemplate.update(order);
  }

  @Override
  public Map<String, OrderData> getOrders() {
    return aerospikeTemplate
        .findAll(OrderData.class)
        .collect(Collectors.toMap(OrderData::getId, order -> order));
  }

  @Override
  public List<OrderData> getOrdersByCustomer(Map<String, String> orderRefsIds) {
    List<String> ids = new ArrayList<>(orderRefsIds.values());

    return aerospikeTemplate
        .findAll(OrderData.class)
        .filter(order -> ids.contains(order.getId()))
        .collect(Collectors.toList());
  }
}

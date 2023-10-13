package de.uniba.dsg.wss.data.access;

import com.aerospike.client.*;
import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  // TODO: HOW TO BATCH WRITE SAVEALL ?!
  @Override
  public void saveAll(Map<String, OrderItemData> idsToOrders) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToOrders.forEach((id, orderItem) -> aerospikeTemplate.save(orderItem));
  }

  // TODO: HOW TO BATCH READ -  getOrderItemsByOrder ?!
  @Override
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
  }

  @Override
  public void storeUpdatedOrderItem(OrderItemData orderItem) {
    aerospikeTemplate.update(orderItem);
  }
  // TODO: HOW TO BATCH WRITE -  saveOrderItemsInBatch ?!
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
}

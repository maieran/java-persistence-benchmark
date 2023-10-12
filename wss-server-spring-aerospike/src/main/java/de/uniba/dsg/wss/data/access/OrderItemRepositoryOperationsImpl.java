package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.OrderItemData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class OrderItemRepositoryOperationsImpl implements OrderItemRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public OrderItemRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

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
}

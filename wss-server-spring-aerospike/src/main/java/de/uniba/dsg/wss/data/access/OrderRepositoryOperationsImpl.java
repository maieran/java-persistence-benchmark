package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.OrderData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

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

  // TODO: HOW TO BATCH READ -  getOrdersFromDistrict ?!
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
  }
}

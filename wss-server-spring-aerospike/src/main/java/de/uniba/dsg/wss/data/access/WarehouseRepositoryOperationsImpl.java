package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

/**
 * Implementation of custom defined operations of {@link WarehouseRepositoryOperations} interface
 * for accessing and modifying {@link WarehouseData warehouses}.
 *
 * @author Andre Maier
 */
public class WarehouseRepositoryOperationsImpl implements WarehouseRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public WarehouseRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, WarehouseData> idsToWarehouse) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToWarehouse.forEach((id, warehouse) -> aerospikeTemplate.save(warehouse));
  }

  @Override
  public Map<String, WarehouseData> getWarehouses() {
    return aerospikeTemplate
        .findAll(WarehouseData.class)
        .collect(Collectors.toMap(WarehouseData::getId, warehouse -> warehouse));
  }
}

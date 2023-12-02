package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;

/**
 * This interface provides custom operations specific to WarehouseData in the Aerospike repository,
 * that are implemented in {@link WarehouseRepositoryOperationsImpl} and are extended by {@link
 * WarehouseRepository}.
 *
 * @author Andre Maier
 */
public interface WarehouseRepositoryOperations {
  void saveAll(Map<String, WarehouseData> idsToWarehouse);

  Map<String, WarehouseData> getWarehouses();
}

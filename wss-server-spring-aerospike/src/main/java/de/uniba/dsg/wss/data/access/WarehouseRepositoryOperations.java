package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;

public interface WarehouseRepositoryOperations {
  void saveAll(Map<String, WarehouseData> idsToWarehouse);

  Map<String, WarehouseData> getWarehouses();
}

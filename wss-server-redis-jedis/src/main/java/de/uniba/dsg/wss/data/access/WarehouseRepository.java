package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;

public interface WarehouseRepository {

  void saveAll(Map<String, WarehouseData> warehouses);

  Map<String, WarehouseData> getWarehouses();

  WarehouseData findById(String id);

  void save(WarehouseData warehouse);

  //  void deleteAll();
}

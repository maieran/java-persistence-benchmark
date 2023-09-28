package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;

public interface CarrierRepositoryOperations {
  void saveAll(Map<String, CarrierData> idsToCarriers);
}

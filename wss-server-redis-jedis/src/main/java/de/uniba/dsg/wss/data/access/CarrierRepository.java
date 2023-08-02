package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link CarrierData carriers}.
 *
 * @author Andre Maier
 */
public interface CarrierRepository {
  void saveAll(Map<String, CarrierData> idsToCarriers);

  Map<String, CarrierData> getCarriers();

  CarrierData findById(String carrierRefId);
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;

/**
 * This interface provides custom operations specific to CarrierData in the Aerospike repository,
 * that are implemented in {@link CarrierRepositoryOperationsImpl} and are extended by {@link
 * CarrierRepository}.
 *
 * @author Andre Maier
 */
public interface CarrierRepositoryOperations {
  void saveAll(Map<String, CarrierData> idsToCarriers);

  Map<String, CarrierData> getCarriers();

  CarrierData findByCarrierId(String carrierRefId);
}

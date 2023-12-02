package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

/**
 * Aerospike repository for accessing and modifying {@link CarrierData carriers}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to carriers by {@link CarrierRepositoryOperations}.
 *
 * @author Andre Maier
 */
public interface CarrierRepository
    extends AerospikeRepository<CarrierData, String>, CarrierRepositoryOperations {

  void saveAll(Map<String, CarrierData> idsToCarriers);

  @Override
  Iterable<CarrierData> findAll();
}

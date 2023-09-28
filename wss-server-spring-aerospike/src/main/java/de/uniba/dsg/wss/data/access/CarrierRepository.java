package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface CarrierRepository
    extends AerospikeRepository<CarrierData, String>, CarrierRepositoryOperations {

  void saveAll(Map<String, CarrierData> idsToCarriers);

  @Override
  Iterable<CarrierData> findAll();
}

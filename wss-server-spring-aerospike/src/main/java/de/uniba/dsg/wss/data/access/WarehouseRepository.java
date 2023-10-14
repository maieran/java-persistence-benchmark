package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface WarehouseRepository
    extends AerospikeRepository<WarehouseData, String>, WarehouseRepositoryOperations {

  void saveAll(Map<String, WarehouseData> warehouses);

  @Override
  Iterable<WarehouseData> findAll();

  @Override
  Optional<WarehouseData> findById(String id);
}

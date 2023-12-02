package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.aerospike.repository.AerospikeRepository;

/**
 * Aerospike repository for accessing and modifying {@link WarehouseData warehouses}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to warehouses by {@link WarehouseRepositoryOperations}.
 *
 * @author Andre Maier
 */
public interface WarehouseRepository
    extends AerospikeRepository<WarehouseData, String>, WarehouseRepositoryOperations {

  void saveAll(Map<String, WarehouseData> warehouses);

  @Override
  Iterable<WarehouseData> findAll();

  @Override
  Optional<WarehouseData> findById(String id);
}

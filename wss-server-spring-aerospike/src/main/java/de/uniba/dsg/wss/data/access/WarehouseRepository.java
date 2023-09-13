package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.Map;
import java.util.Optional;

public interface WarehouseRepository extends AerospikeRepository<WarehouseData, Integer> {

    <S extends WarehouseData> Iterable<S> saveAll(Map<String, WarehouseData> warehouses);

    @Override
    Iterable<WarehouseData> findAll();

    @Override
    Optional<WarehouseData> findById(Integer integer);


}

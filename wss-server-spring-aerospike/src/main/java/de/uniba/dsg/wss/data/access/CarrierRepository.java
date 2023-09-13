package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.Map;

public interface CarrierRepository extends AerospikeRepository<CarrierData, Integer> {



    <S extends CarrierData> Iterable<S> saveAll(Map<String, CarrierData> idsToCarriers);

    @Override
    Iterable<CarrierData> findAll();
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(readOnly = true)
public interface CustomerRepository extends AerospikeRepository<CustomerData, Integer> {
    <S extends CustomerData> Iterable<S> saveAll(Map<String, CustomerData> idsToCustomers);
}

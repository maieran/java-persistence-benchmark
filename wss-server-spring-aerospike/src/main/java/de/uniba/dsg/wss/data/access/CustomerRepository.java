package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CustomerRepository extends AerospikeRepository<CustomerData, Integer> {
  <S extends CustomerData> Iterable<S> saveAll(Map<String, CustomerData> idsToCustomers);
}

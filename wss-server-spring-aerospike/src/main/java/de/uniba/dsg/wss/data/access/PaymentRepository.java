package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface PaymentRepository extends AerospikeRepository<PaymentData, Integer> {
  <S extends PaymentData> Iterable<S> saveAll(Map<String, PaymentData> idsToPayments);
}

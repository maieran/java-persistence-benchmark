package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.PaymentData;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.Map;

public interface PaymentRepository extends AerospikeRepository<PaymentData, Integer> {
    <S extends PaymentData> Iterable<S> saveAll(Map<String, PaymentData> idsToPayments);
}

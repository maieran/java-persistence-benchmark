package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;

import de.uniba.dsg.wss.data.model.StockData;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface PaymentRepository extends AerospikeRepository<PaymentData, String>, PaymentRepositoryOperations {
  void saveAll(Map<String, PaymentData> idsToPayments);


}

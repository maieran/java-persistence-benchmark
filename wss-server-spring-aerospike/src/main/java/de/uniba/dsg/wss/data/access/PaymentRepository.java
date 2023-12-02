package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

/**
 * Aerospike repository for accessing and modifying {@link PaymentData payments}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to payments by {@link PaymentRepositoryOperations}.
 *
 * @author Andre Maier
 */
public interface PaymentRepository
    extends AerospikeRepository<PaymentData, String>, PaymentRepositoryOperations {
  void saveAll(Map<String, PaymentData> idsToPayments);
}

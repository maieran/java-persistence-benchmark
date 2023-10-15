package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;

/**
 * This interface provides custom operations specific to PaymentData in the Aerospike repository,
 * that are implemented in {@link PaymentRepositoryOperationsImpl} and are extended by {@link
 * OrderRepository}.
 *
 * @author Andre Maier
 */
public interface PaymentRepositoryOperations {
  void saveAll(Map<String, PaymentData> idsToPayments);

  Map<String, PaymentData> getPayments();
}

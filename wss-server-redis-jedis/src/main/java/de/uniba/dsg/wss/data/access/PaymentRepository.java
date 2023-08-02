package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;

import de.uniba.dsg.wss.data.model.StockData;
import org.springframework.stereotype.Repository;

/**
 * Redis repository for accessing and modifying {@link PaymentData payments}.
 *
 * @author Andre Maier
 */
@Repository
public interface PaymentRepository {
  void saveAll(Map<String, PaymentData> idsToPayments);

  void save(PaymentData payment);

  Map<String, PaymentData> getPayments();
}

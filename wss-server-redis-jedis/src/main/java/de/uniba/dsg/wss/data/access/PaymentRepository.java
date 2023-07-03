package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {
  void saveAll(Map<String, PaymentData> idsToPayments);
}

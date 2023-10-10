package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class PaymentRepositoryOperationsImpl implements PaymentRepositoryOperations {

  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public PaymentRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, PaymentData> idsToPayments) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToPayments.forEach((id, payment) -> aerospikeTemplate.save(payment));
  }
}

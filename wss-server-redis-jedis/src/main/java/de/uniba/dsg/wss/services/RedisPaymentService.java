package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class RedisPaymentService extends PaymentService {
  @Override
  public PaymentResponse process(PaymentRequest paymentRequest) {
    return null;
  }
}

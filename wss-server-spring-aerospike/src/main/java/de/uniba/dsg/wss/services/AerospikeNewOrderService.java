package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.service.NewOrderService;
import org.springframework.stereotype.Service;

@Service
public class AerospikeNewOrderService extends NewOrderService {
  @Override
  public NewOrderResponse process(NewOrderRequest newOrderRequest) {
    return null;
  }
}

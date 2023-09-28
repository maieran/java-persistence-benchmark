package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import org.springframework.stereotype.Service;

@Service
public class AerospikeDeliveryService extends DeliveryService {
  @Override
  public DeliveryResponse process(DeliveryRequest deliveryRequest) {
    return null;
  }
}

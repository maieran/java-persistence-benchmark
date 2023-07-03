package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.service.OrderStatusService;
import org.springframework.stereotype.Service;

@Service
public class RedisOrderStatusService extends OrderStatusService {
  @Override
  public OrderStatusResponse process(OrderStatusRequest orderStatusRequest) {
    return null;
  }
}

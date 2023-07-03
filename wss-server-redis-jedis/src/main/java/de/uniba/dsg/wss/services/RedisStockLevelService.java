package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import org.springframework.stereotype.Service;

@Service
public class RedisStockLevelService extends StockLevelService {
  @Override
  public StockLevelResponse process(StockLevelRequest stockLevelRequest) {
    return null;
  }
}

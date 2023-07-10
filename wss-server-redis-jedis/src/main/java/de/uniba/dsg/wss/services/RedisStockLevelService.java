package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisStockLevelService extends StockLevelService {

  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final StockRepository stockRepository;

  @Autowired
  public RedisStockLevelService(
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      StockRepository stockRepository) {
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.stockRepository = stockRepository;
  }

  @Override
  public StockLevelResponse process(StockLevelRequest stockLevelRequest) {
    WarehouseData warehouse = warehouseRepository.findById(stockLevelRequest.getWarehouseId());
    DistrictData district = districtRepository.findById(stockLevelRequest.getDistrictId());

    List<String> districtRefsIds = warehouse.getDistrictRefsIds();
    if (districtRefsIds.contains(district.getId())) {
      List<StockData> stocksInOrder = new ArrayList<>();
      List<OrderData> orders = orderRepository.getOrdersFromDistrict(district.getOrderRefsIds());
      orders.sort(Comparator.comparing(OrderData::getEntryDate));
      int limit = Math.min(orders.size(), 20);

      for (int i = 0; i < limit; i++) {
        OrderData order = orders.get(i);
        List<String> orderItemsIds = order.getItemsIds();
        // List<StockData> orderStocks = new ArrayList<>();
        Map<String, StockData> allStock = stockRepository.getStocks();

        for (String orderItemId : orderItemsIds) {
          OrderItemData orderItem = orderItemRepository.findById(orderItemId);
          for (Map.Entry<String, StockData> entry : allStock.entrySet()) {
            if (orderItem.getSupplyingWarehouseRefId().equals(entry.getValue().getWarehouseRefId())
                && orderItem.getProductRefId().equals(entry.getValue().getProductRefId())) {
              stocksInOrder.add(entry.getValue());
            }
          }
        }
      }

      List<StockData> distinctStocks = new ArrayList<>();
      for (StockData stock : stocksInOrder) {
        if (!distinctStocks.contains(stock)) {
          distinctStocks.add(stock);
        }
      }

      stocksInOrder = distinctStocks;

      int lowStockCount =
          countStockEntriesLowerThanThreshold(stocksInOrder, stockLevelRequest.getStockThreshold());
      return new StockLevelResponse(stockLevelRequest, lowStockCount);

    } else {
      throw new IllegalArgumentException("There is no such district in the given warehouse");
    }
  }

  // TODO: CHECK HOW IT WILL BEHAVE WHEN CONCURRENCY
  private int countStockEntriesLowerThanThreshold(
      List<StockData> stocksInOrder, int stockThreshold) {
    return (int)
        stocksInOrder.parallelStream().filter(s -> s.getQuantity() < stockThreshold).count();
  }
}

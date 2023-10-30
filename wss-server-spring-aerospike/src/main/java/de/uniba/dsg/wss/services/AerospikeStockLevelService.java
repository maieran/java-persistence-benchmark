package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link StockLevelService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 * @author Andre Maier
 */
@Service
public class AerospikeStockLevelService extends StockLevelService {
  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final StockRepository stockRepository;

  @Autowired
  public AerospikeStockLevelService(
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
    Optional<WarehouseData> warehouse =
        warehouseRepository.findById(stockLevelRequest.getWarehouseId());
    Optional<DistrictData> district =
        districtRepository.findById(stockLevelRequest.getDistrictId());

    List<String> districtRefsIds = warehouse.get().getDistrictRefsIds();
    if (districtRefsIds.contains(district.get().getId())) {
      List<StockData> stocksInOrder = new ArrayList<>();

      List<OrderData> orders =
          orderRepository.getOrdersFromDistrict(district.get().getOrderRefsIds());
      orders.sort(Comparator.comparing(OrderData::getEntryDate));
      int limit = Math.min(orders.size(), 20);

      for (int i = 0; i < limit; i++) {
        OrderData order = orders.get(i);

        Map<String, StockData> allStock = stockRepository.getStocks();
        List<OrderItemData> orderItems =
            orderItemRepository.getOrderItemsByOrder(order.getItemsIds());

        for (OrderItemData orderItem : orderItems) {
          for (Map.Entry<String, StockData> entry : allStock.entrySet()) {
            if (orderItem.getSupplyingWarehouseRefId().equals(entry.getValue().getWarehouseRefId())
                && orderItem.getProductRefId().equals(entry.getValue().getProductRefId())) {
              stocksInOrder.add(entry.getValue());
            }
          }
        }
      }

      List<StockData> distinctStocks = new ArrayList<>();
      Set<String> seenStocks = new HashSet<>();

      for (StockData stock : stocksInOrder) {
        String stockId = stock.getId();
        if (!seenStocks.contains(stockId)) {
          distinctStocks.add(stock);
          seenStocks.add(stockId);
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

  private int countStockEntriesLowerThanThreshold(
      List<StockData> stocksInOrder, int stockThreshold) {
    return (int)
        stocksInOrder.parallelStream().filter(s -> s.getQuantity() < stockThreshold).count();
  }
}

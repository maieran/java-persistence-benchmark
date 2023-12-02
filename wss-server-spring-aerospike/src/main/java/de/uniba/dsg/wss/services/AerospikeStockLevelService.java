package de.uniba.dsg.wss.services;

import de.uniba.dsg.wss.data.access.*;
import de.uniba.dsg.wss.data.model.*;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import java.util.*;
import java.util.stream.Collectors;
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

    List<String> districtRefsIds = warehouse.orElseThrow().getDistrictRefsIds();
    if (districtRefsIds.contains(district.orElseThrow().getId())) {
      List<StockData> stocksInOrder =
          orderRepository.getOrdersFromDistrict(district.orElseThrow().getOrderRefsIds()).stream()
              .sorted(Comparator.comparing(OrderData::getEntryDate))
              .limit(20)
              .flatMap(
                  order -> {
                    Map<String, StockData> allStock = stockRepository.getStocks();
                    List<OrderItemData> orderItems =
                        orderItemRepository.getOrderItemsByOrder(order.getItemsIds());
                    return orderItems.stream()
                        .filter(
                            orderItem ->
                                allStock.containsKey(orderItem.getSupplyingWarehouseRefId()))
                        .map(orderItem -> allStock.get(orderItem.getSupplyingWarehouseRefId()));
                  })
              .distinct()
              .collect(Collectors.toList());

      int lowStockCount =
          countStockEntriesLowerThanThreshold(stocksInOrder, stockLevelRequest.getStockThreshold());
      return new StockLevelResponse(stockLevelRequest, lowStockCount);
    } else {
      throw new IllegalArgumentException("There is no such district in the given warehouse");
    }
  }

  private int countStockEntriesLowerThanThreshold(
      List<StockData> stocksInOrder, int stockThreshold) {
    return (int) stocksInOrder.stream().filter(s -> s.getQuantity() < stockThreshold).count();
  }
}

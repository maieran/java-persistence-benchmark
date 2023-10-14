package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.wss.AerospikeTest;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.services.AerospikeNewOrderService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AerospikeNewOrderIntegrationTests extends AerospikeTest {

  @Autowired AerospikeNewOrderService aerospikeNewOrderService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  private void adjustDefaults(int retries, int increaseQuantity) {
    AerospikeNewOrderService.maxRetries = retries;
    StockData.increaseQuantity = increaseQuantity;
  }

  static class ProductToOrder {
    protected String stockId;
    protected String warehouseId;
    protected String productId;
    protected int quantity;

    public ProductToOrder(String warehouseId, String productId, int quantity) {
      // remember the optimization :)
      this.stockId = warehouseId + productId;
      this.warehouseId = warehouseId;
      this.productId = productId;
      this.quantity = quantity;
    }
  }

  private NewOrderRequest getNewOrderRequest(
      String warehouseId,
      String districtId,
      String customerId,
      List<ProductToOrder> productToOrderList) {
    NewOrderRequest request = new NewOrderRequest();
    request.setWarehouseId(warehouseId);
    request.setDistrictId(districtId);
    request.setCustomerId(customerId);

    List<NewOrderRequestItem> items = new ArrayList<>();
    for (ProductToOrder productsToOrder : productToOrderList) {
      String supplyingWarehouseId = productsToOrder.warehouseId;
      String productId = productsToOrder.productId;
      NewOrderRequestItem item = new NewOrderRequestItem();
      item.setProductId(productId);
      item.setQuantity(productsToOrder.quantity);
      item.setSupplyingWarehouseId(supplyingWarehouseId);
      items.add(item);
    }
    request.setItems(items);
    return request;
  }

  @Test
  public void invalidWarehouse() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // key -> stock id, value -> quantity
          List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));

          NewOrderRequest request = getNewOrderRequest("WW0", "D0", "C0", productToOrderList);
          this.aerospikeNewOrderService.process(request);
        });
  }

  @Test
  public void invalidDistrict() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // key -> stock id, value -> quantity
          List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));

          NewOrderRequest request = getNewOrderRequest("W0", "DD0", "C0", productToOrderList);
          this.aerospikeNewOrderService.process(request);
        });
  }

  @Test
  public void invalidCustomer() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // key -> stock id, value -> quantity
          List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));

          NewOrderRequest request = getNewOrderRequest("W0", "D0", "CC0", productToOrderList);
          this.aerospikeNewOrderService.process(request);
        });
  }

  @Test
  public void invalidStockNumber() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // key -> stock id, value -> quantity
          // stock does not exist
          List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P0", 2));

          NewOrderRequest request = getNewOrderRequest("W0", "D0", "C0", productToOrderList);
          this.aerospikeNewOrderService.process(request);
        });
  }

  @Test
  public void processingNewOrderConcurrently() throws InterruptedException {
    adjustDefaults(5, 1000);

    // key -> stock id, value -> quantity
    List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));
    NewOrderRequest request = getNewOrderRequest("W0", "D0", "C0", productToOrderList);
    int concurrentRequests = 500;
    ExecutorService executorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    for (int i = 0; i < concurrentRequests; i++) {
      executorService.execute(() -> this.aerospikeNewOrderService.process(request));
    }

    this.shutdownAndAwaitTermination(executorService);
    assertEquals(2, this.stockRepository.getStocks().get("W1P1").getQuantity());
  }

  void shutdownAndAwaitTermination(ExecutorService pool) {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(60, TimeUnit.MINUTES)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  @Test
  public void processingPersistsNewOrder() {

    // key -> stock id, value -> quantity
    List<ProductToOrder> productToOrderList =
        List.of(
            new ProductToOrder("W1", "P1", 2),
            new ProductToOrder("W2", "P6", 4),
            new ProductToOrder("W0", "P8", 5),
            new ProductToOrder("W3", "P1", 1));

    NewOrderRequest request = getNewOrderRequest("W0", "D0", "C0", productToOrderList);
    this.aerospikeNewOrderService.process(request);

    int sizeOfOrders = this.orderRepository.getOrders().size();
    assertEquals(21, sizeOfOrders);
    /**
     * direct invoking of getOrders() leads to anomalies in size, perhaps due to interceptors and
     * proxies of aop when calling redis directly
     */
    // assertEquals(21, this.orderRepository.getOrders().size(););
  }
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.List;
import java.util.Map;

public interface ProductRepositoryOperations {
  void saveAll(Map<String, ProductData> idsToProducts);

  Map<String, ProductData> getProductsFromStocks(List<String> productIds);
}

package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * Redis repository for accessing and modifying {@link ProductData products}.
 *
 * @author Andre Maier
 */
public interface ProductRepository {
  void saveAll(Map<String, ProductData> idsToProducts);

  Map<String, ProductData> getAllProducts();

  ProductData findById(String productRefId);

  Map<String, ProductData> getProductsFromStocks(List<String> productIds);
}

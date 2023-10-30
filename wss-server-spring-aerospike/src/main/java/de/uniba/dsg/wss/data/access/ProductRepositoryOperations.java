package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.List;
import java.util.Map;

/**
 * This interface provides custom operations specific to ProductData in the Aerospike repository,
 * that are implemented in {@link ProductRepositoryOperationsImpl} and are extended by {@link
 * ProductRepository}.
 *
 * @author Andre Maier
 */
public interface ProductRepositoryOperations {
  void saveAll(Map<String, ProductData> idsToProducts);

  Map<String, ProductData> getProductsFromStocks(List<String> productIds);
}

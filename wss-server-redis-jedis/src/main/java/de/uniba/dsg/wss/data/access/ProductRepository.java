package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.Map;

public interface ProductRepository {
  void saveAll(Map<String, ProductData> idsToProducts);

  Map<String, ProductData> getAllProducts();

  ProductData findById(String productRefId);
}

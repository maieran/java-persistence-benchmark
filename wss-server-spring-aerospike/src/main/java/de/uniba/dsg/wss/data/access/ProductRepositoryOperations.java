package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.Map;

public interface ProductRepositoryOperations {
  void saveAll(Map<String, ProductData> idsToProducts);
}

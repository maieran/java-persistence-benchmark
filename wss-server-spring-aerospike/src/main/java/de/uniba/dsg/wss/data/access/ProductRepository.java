package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface ProductRepository
    extends AerospikeRepository<ProductData, String>, ProductRepositoryOperations {

  void saveAll(Map<String, ProductData> idsToProducts);

  @Override
  Iterable<ProductData> findAll();
}

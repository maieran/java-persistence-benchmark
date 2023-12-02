package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;

/**
 * Aerospike repository for accessing and modifying {@link ProductData products}. Extending this
 * interface is providing basic CRUD operations by {@link AerospikeRepository} as well as adding
 * custom operations specific to products by {@link ProductRepositoryOperations}.
 *
 * @author Andre Maier
 */
public interface ProductRepository
    extends AerospikeRepository<ProductData, String>, ProductRepositoryOperations {

  void saveAll(Map<String, ProductData> idsToProducts);

  @Override
  Iterable<ProductData> findAll();
}

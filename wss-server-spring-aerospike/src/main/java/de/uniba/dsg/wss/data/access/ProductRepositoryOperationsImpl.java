package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.ProductData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeOperations;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class ProductRepositoryOperationsImpl implements ProductRepositoryOperations {

  // private final AerospikeOperations aerospikeOperations;
  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public ProductRepositoryOperationsImpl(
      AerospikeOperations aerospikeOperations, AerospikeTemplate aerospikeTemplate) {
    // this.aerospikeOperations = aerospikeOperations;
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, ProductData> idsToProducts) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    // aerospikeTemplate.persist(idsToProducts, writePolicy);

    idsToProducts.forEach((id, product) -> aerospikeTemplate.save(product));
    // aerospikeTemplate.save(idsToProducts);
    /*         idsToProducts.forEach((id, product) ->
            aerospikeTemplate.save(product, writePolicy, product.getId())
    );*/
  }

  // TODO: HOW TO BATCH READ - getProductsFromStocks ?!
  @Override
  public Map<String, ProductData> getProductsFromStocks(List<String> productIds) {
    Map<String, ProductData> products = new HashMap<>();

    // Iterate over the district reference IDs and read each record individually
    for (String id : productIds) {
      // Read the record for the key
      ProductData product = aerospikeTemplate.findById(id, ProductData.class);

      // Check if the record exists
      if (product != null) {
        products.put(product.getId(), product);
      }
    }

    return products;
  }
}

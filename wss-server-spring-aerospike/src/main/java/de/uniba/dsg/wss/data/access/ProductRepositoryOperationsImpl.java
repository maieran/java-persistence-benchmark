package de.uniba.dsg.wss.data.access;

import com.aerospike.client.Key;
import com.aerospike.client.Record;
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

    idsToProducts.forEach((id, product) -> aerospikeTemplate.save(product));
  }

  @Override
  public Map<String, ProductData> getProductsFromStocks(List<String> productIds) {
    Map<String, ProductData> products = new HashMap<>();

    // 1.Step - Collect the keys/ids necessary to retrieve the objects
    Key[] keys = new Key[productIds.size()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] =
          new Key(
              aerospikeTemplate.getNamespace(),
              aerospikeTemplate.getSetName(ProductData.class),
              productIds.get(i));
    }

    // 2.Step - Retrieve productData from Aerospike data model
    Record[] records = aerospikeTemplate.getAerospikeClient().get(null, keys);

    // 3.Step - Populate the list of stocks
    for (int i = 0; i < records.length; i++) {
      Record record = records[i];
      if (record != null) {

        // Create the StockData instance
        ProductData product =
            new ProductData(
                productIds.get(i), // Set the id using the productIds list
                record.getString("imagePath"),
                record.getString("name"),
                record.getDouble("price"),
                record.getString("data"));

        products.put(productIds.get(i), product);
      }
    }

    return products;
  }

  /*
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
  }*/
}

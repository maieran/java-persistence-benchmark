package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This initializer uses the {@link JacksonParser} to deserialize the stored JSON files into
 * supplier data model as {@link DataGeneratorModel} and writes it to the configured Redis storage.
 *
 * @author Andre Maier
 */
@Component
@ConditionalOnProperty(name = "wss.model.initialize", havingValue = "true")
public class RedisDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(RedisDataInitializer.class);
  private final RedisDataWriter dataWriter;

  @Autowired
  public RedisDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, RedisDataWriter dataWriter) {
    super(environment, passwordEncoder);
    this.dataWriter = dataWriter;
  }

  @Override
  public void initializePersistentData() throws IOException {

    JacksonParser jacksonParser = new JacksonParser();

    // Get the current working directory
    String currentDir = System.getProperty("user.dir");

    List<Product> productsList =
        jacksonParser.deserializeProductsFromJSON(
            currentDir + File.separator + "baseline-model_products.json");

    List<Warehouse> warehouseList =
        jacksonParser.deserializeWarehousesFromJSON(
            currentDir + File.separator + "baseline-model_warehouses.json");

    List<Employee> employeeList =
        jacksonParser.deserializeEmployeesFromJSON(
            currentDir + File.separator + "baseline-model_employees.json");

    List<Carrier> carrierList =
        jacksonParser.deserializeCarriersFromJSON(
            currentDir + File.separator + "baseline-model_carriers.json");

    Stats stats =
        jacksonParser.deserializeStatsFromJSON(
            currentDir + File.separator + "baseline-model_stats.json");

    DataGeneratorModel deserializedModel =
        new DataGeneratorModel(productsList, warehouseList, employeeList, carrierList, stats);

    RedisDataModel redisDataModel = new RedisDataConverter().convert(deserializedModel);
    stats = null;
    carrierList.clear();
    productsList.clear();
    employeeList.clear();
    warehouseList.clear();
    jacksonParser = null;
    dataWriter.write(redisDataModel);

    LOG.info(
        "Total amount of deserialized objects: {} for Redis",
        redisDataModel.getStats().getTotalModelObjectCount());
  }
}

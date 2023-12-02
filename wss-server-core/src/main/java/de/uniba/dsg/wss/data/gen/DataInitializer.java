package de.uniba.dsg.wss.data.gen;

import static org.apache.logging.log4j.util.Unbox.box;

import de.uniba.dsg.wss.auth.Roles;
import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementations should generate an initial set of wholesale supplier model data and write this
 * data to persistent storage.
 *
 * <p>Additions: A similar supplier model data can now be generated for every persistence solution
 * with the help of serialization and deserialization by {@link JacksonParser} in order to ensure
 * comparability in benchmarking.
 *
 * @author Benedikt Full
 * @author Andre Maier
 */
public abstract class DataInitializer implements CommandLineRunner {

  private static final Logger LOG = LogManager.getLogger(DataInitializer.class);
  private final PasswordEncoder passwordEncoder;
  private final int modelWarehouseCount;
  private final boolean fullScaleModel;

  public DataInitializer(Environment environment, PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
    modelWarehouseCount = environment.getProperty("wss.model.warehouse-count", Integer.class, 1);
    fullScaleModel = environment.getProperty("wss.model.full-scale", Boolean.class, true);
  }

  /**
   * Returns a new {@link DefaultDataGenerator} based on the configuration properties read by this
   * instance.
   *
   * <p>Multiple calls to this method on the same bean will always return a new object, but the
   * configuration of the objects will remain the same.
   *
   * @return a new data generator
   * @see #generateData()
   */
  protected DefaultDataGenerator createDataGenerator() {
    return new DefaultDataGenerator(
        modelWarehouseCount, fullScaleModel, passwordEncoder::encode, Roles.TERMINAL_USER);
  }

  /**
   * Creates a new {@link DefaultDataGenerator} by using {@link #createDataGenerator()}, calls its
   * {@link DefaultDataGenerator#generate() generate()} method and returns the result of the method.
   *
   * <p>Beforehand, it checks if JSON files from previous run of the application have not been
   * created, so that they won't be overwritten with the next start of the program and therefore
   * performs a log message to the user. To generate a new data model, the user must delete the
   * existing JSON files in the current working directory of the project.
   *
   * <p>Note that as the generator itself does not perform any logging, this method uses the data
   * provided by the generator for some intermediate logging.
   *
   * @return a newly generated data model
   */
  protected DataModel<Product, Warehouse, Employee, Carrier> generateData() {

    DataModel<Product, Warehouse, Employee, Carrier> model;

    // Get the current working directory
    String currentDir = System.getProperty("user.dir");
    String directoryName = "baseline-model-dir";

    String[] jsonFiles = {
      "baseline-model_products.json",
      "baseline-model_warehouses.json",
      "baseline-model_carriers.json",
      "baseline-model_employees.json",
      "baseline-model_stats.json"
    };

    for (String filename : jsonFiles) {
      File file = new File(currentDir + File.separator + directoryName + File.separator + filename);
      if (file.exists()) {
        LOG.info("Baseline model files already exist. Skipping data generation.");
        return null;
      }
    }

    DefaultDataGenerator generator = createDataGenerator();
    Configuration config = generator.getConfiguration();
    LOG.info(
        "Generating {} products, {} warehouses, {} districts, {} employees, {} customers, and {} orders",
        box(config.getProductCount()),
        box(config.getWarehouseCount()),
        box(config.getDistrictCount()),
        box(config.getEmployeeCount()),
        box(config.getCustomerCount()),
        box(config.getOrderCount()));
    model = generator.generate();
    LOG.info(
        "Generated {} model data objects, took {}",
        box(model.getStats().getTotalModelObjectCount()),
        model.getStats().getDuration());

    return model;
  }

  /**
   * Callback used to run the bean. Initializes the initial persistent data state using the {@link
   * #baseModelFileGeneration()}, where the data is serialized by {@link JacksonParser} and is
   * written to JSON files. If the JSON files exist, then serialization is skipped and user is
   * informed via a log. The implementation of {@link #initializePersistentData()} provides the
   * deserialization of the JSON files to Java objects in accordance with {@link DataGeneratorModel}
   * in any persistence solution in the application.
   *
   * @param args are ignored
   * @throws Exception on error
   */
  @Override
  public void run(String... args) throws Exception {
    baseModelFileGeneration();
    initializePersistentData();
  }

  /**
   * Initializes the data generation and uses {@link JacksonParser} to serialize generated data
   * model by {@link DataModel} into a JSON-format file. It dissects and stores the model in five
   * JSON-files as lists, which can be reused for deserialization with the help of incremental
   * streaming api in {@link #initializePersistentData()} by Jackson library in {@link
   * JacksonParser} for any persistence solution.
   *
   * @throws IOException on error
   */
  protected void baseModelFileGeneration() throws IOException {
    DataModel<Product, Warehouse, Employee, Carrier> model = generateData();

    if (model != null) {

      // Get the current working directory
      String currentDir = System.getProperty("user.dir");
      String directoryName = "baseline-model-dir";

      JacksonParser jacksonParser = new JacksonParser();

      jacksonParser.serializeProductsToJSON(
          model.getProducts(),
          currentDir
              + File.separator
              + directoryName
              + File.separator
              + "baseline-model_products.json");

      jacksonParser.serializeWarehousesToJSON(
          model.getWarehouses(),
          currentDir
              + File.separator
              + directoryName
              + File.separator
              + "baseline-model_warehouses.json");

      jacksonParser.serializeEmployeesToJSON(
          model.getEmployees(),
          currentDir
              + File.separator
              + directoryName
              + File.separator
              + "baseline-model_employees.json");

      jacksonParser.serializeCarriersToJSON(
          model.getCarriers(),
          currentDir
              + File.separator
              + directoryName
              + File.separator
              + "baseline-model_carriers.json");

      jacksonParser.serializeStatsToJSON(
          model.getStats(),
          currentDir
              + File.separator
              + directoryName
              + File.separator
              + "baseline-model_stats.json");
    }
  }

  /**
   * Implementations of this method must use {@link JacksonParser} for the deserialization of the
   * existing or newly generated JSON files to reconstruct the supplier data model in accordance
   * with {@link DataGeneratorModel}. Then, in conjunction with the appropriate {@link
   * DataConverter} implementation it generates the type of model data required by the backing
   * persistence solution. The converted data must be written to persistent storage using a {@link
   * DataWriter} implementation.
   *
   * @throws Exception on error
   */
  public abstract void initializePersistentData() throws Exception;
}

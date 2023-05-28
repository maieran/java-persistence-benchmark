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
 * @author Benedikt Full
 */
public abstract class DataInitializer implements CommandLineRunner {

  private static final Logger LOG = LogManager.getLogger(DataInitializer.class);
  private final PasswordEncoder passwordEncoder;
  private final int modelWarehouseCount;
  private final boolean fullScaleModel;
  private String filePath;

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
   * <p>Note that as the generator itself does not perform any logging, this method uses the data
   * provided by the generator for some intermediate logging.
   *
   * @return a newly generated data model
   */
  protected DataModel<Product, Warehouse, Employee, Carrier> generateData() {

    DataModel<Product, Warehouse, Employee, Carrier> model;

    // Get the current working directory
    String currentDir = System.getProperty("user.dir");
    // Specify the relative path and filename
    String filePath = currentDir + File.separator + "baseline-model.json";

    File file = new File(filePath);

    if (!file.exists()) {
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
    LOG.info("File exists: {}, no generation needed", file);
    return null;
  }

  /**
   * Callback used to run the bean. Initializes the initial persistent data state using the
   * implementation of {@link #initializePersistentData()}.
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
   * model by {@link DataModel} into a JSON-format file. It stores then the json file, which ran be
   * reused for deserialization and object mapping in {@link #initializePersistentData()} in the
   * corresponding subclass modules.
   *
   * @throws IOException on error
   */
  protected void baseModelFileGeneration() throws IOException {
    DataModel<Product, Warehouse, Employee, Carrier> model = generateData();

    if (model != null) {

      JacksonParser jacksonParser = new JacksonParser();

      // Get the current working directory
      String currentDir = System.getProperty("user.dir");

      // Specify the relative path and filename
      String filePath = currentDir + File.separator + "baseline-model.json";

      jacksonParser.serialize(model, filePath);
    }
  }

  /**
   * Implementations of this method must use the {@link DefaultDataGenerator} provided by {@link
   * #createDataGenerator()} or {@link #generateData()} in conjunction with the appropriate {@link
   * DataConverter} implementation to generate the type of model data required by the backing
   * persistence solution. The converted data must be written to persistent storage using a {@link
   * DataWriter} implementation.
   *
   * @throws Exception on error
   */
  public abstract void initializePersistentData() throws Exception;
}

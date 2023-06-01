package de.uniba.dsg.wss.data.gen;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This initializer generates a data model based on the associated configuration properties and
 * writes it to the configured JPA-based persistence solution.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "wss.model.initialize", havingValue = "true")
public class JpaDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(JpaDataInitializer.class);
  private final JpaDataWriter databaseWriter;

  @Autowired
  public JpaDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, JpaDataWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void initializePersistentData() throws IOException {
    JacksonParser jacksonParser = new JacksonParser();

    // Get the current working directory
    String currentDir = System.getProperty("user.dir");
    // Specify the relative path and filename
    String filePath = currentDir + File.separator + "baseline-model.json";

    ObjectMapper objectMapper = ObjectMapperHolder.getObjectMapper();

    DataModel<Product, Warehouse, Employee, Carrier> model =
        jacksonParser.deserialize(filePath, objectMapper);
    JpaDataModel entityModel = new JpaDataConverter().convert(model);
    databaseWriter.write(entityModel);

    LOG.info(
        "Total amount of deserialized objects: {} for JPA-PostgreSQL",
        entityModel.getStats().getTotalModelObjectCount());
  }
}

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
 * writes it to the configured MicroStream storage.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "wss.model.initialize", havingValue = "true")
public class MsDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(MsDataInitializer.class);
  private final MsDataWriter dataWriter;

  @Autowired
  public MsDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, MsDataWriter dataWriter) {
    super(environment, passwordEncoder);
    this.dataWriter = dataWriter;
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
    MsDataModel msDataModel = new MsDataConverter().convert(model);
    dataWriter.write(msDataModel);
    LOG.info(
        "Total amount of deserialized objects: {} for MS-Jacis",
        msDataModel.getStats().getTotalModelObjectCount());
  }
}

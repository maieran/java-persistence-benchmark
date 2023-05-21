package de.uniba.dsg.wss.data.gen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JacksonParser {

  private static final Logger LOG = LogManager.getLogger(JacksonParser.class);

  public void serialize(DataModel<?, ?, ?, ?> model, String filePath) throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule()); //
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); //
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); //
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //

      // objectMapper.addMixIn(DataModel.class, DataModelMixin.class);

      objectMapper.writeValue(file, model);
      LOG.info("Serialized data model to file: {}", filePath);
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  public DataModel<Product, Warehouse, Employee, Carrier> deserialize(String filePath)
      throws IOException {
    LOG.info("Data model deserializing from file: {} to Java Objects", filePath);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    List<Product> products = new ArrayList<>();
    List<Warehouse> warehouses = new ArrayList<>();
    List<Employee> employees = new ArrayList<>();
    List<Carrier> carriers = new ArrayList<>();
    Stats stats = new Stats();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      JsonFactory jsonFactory = objectMapper.getFactory();
      JsonParser jsonParser = jsonFactory.createParser(reader);

      // Read the start of the JSON object
      if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
        throw new IOException("Invalid JSON file format. Expected the start of an object.");
      }

      while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = jsonParser.getCurrentName();

        // Read the "products" array
        if ("products".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() == JsonToken.END_ARRAY) {
            Product product = objectMapper.readValue(jsonParser, Product.class);
            products.add(product);
            jsonParser.nextToken(); // Advance to the next token
            LOG.info("Product added to model");
          }
        }

        // Read the "warehouses" array
        else if ("warehouses".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Warehouse warehouse = objectMapper.readValue(jsonParser, Warehouse.class);
            warehouses.add(warehouse);
            jsonParser.nextToken(); // Advance to the next token
            LOG.info("Warehouse added to model");
          }
        }

        // Read the "employees" array
        else if ("employees".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Employee employee = objectMapper.readValue(jsonParser, Employee.class);
            employees.add(employee);
            jsonParser.nextToken(); // Advance to the next token
            LOG.info("Employee added to model");
          }
        }

        // Read the "carriers" array
        else if ("carriers".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Carrier carrier = objectMapper.readValue(jsonParser, Carrier.class);
            carriers.add(carrier);
            jsonParser.nextToken(); // Advance to the next token
            LOG.info("Carrier added to model");
          }
        }

        // Read the "stats" object
        else if ("stats".equals(fieldName)) {
          jsonParser.nextToken();
          stats = objectMapper.readValue(jsonParser, Stats.class);
          LOG.info("Stats added to model");
        }
      }
    }

    return new DataGeneratorModel(products, warehouses, employees, carriers, stats);
  }
}

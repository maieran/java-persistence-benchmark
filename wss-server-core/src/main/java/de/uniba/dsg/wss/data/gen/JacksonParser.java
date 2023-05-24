package de.uniba.dsg.wss.data.gen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.model.*;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JacksonParser {

  private static final Logger LOG = LogManager.getLogger(JacksonParser.class);

  public void serialize(DataModel<?, ?, ?, ?> model, String filePath) throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      Stopwatch stopwatch = new Stopwatch().start();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule()); //
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); //
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); //
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //

      objectMapper.writeValue(file, model);
      stopwatch.stop();

      LOG.info("Serialized data model to file: {} and took {}", filePath, stopwatch.getDuration());
    } else {
      LOG.warn("File already exists and will be taken for database modelling: {}", filePath);
    }
  }

  public DataModel<Product, Warehouse, Employee, Carrier> deserialize(String filePath)
      throws IOException {
    LOG.info("Data model deserializing from file: {} to Java Objects", filePath);

    Stopwatch stopwatch = new Stopwatch().start();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    List<Product> products = new ArrayList<>();
    List<Warehouse> warehouses = new ArrayList<>();
    List<Employee> employees = new ArrayList<>();
    List<Carrier> carriers = new ArrayList<>();
    Stats stats = new Stats();

    Map<String, District> districtMap = new HashMap<>();

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
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Product product = objectMapper.readValue(jsonParser, Product.class);
            products.add(product);
          }
        }

        // Read the "warehouses" array
        else if ("warehouses".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Warehouse warehouse = objectMapper.readValue(jsonParser, Warehouse.class);
            warehouses.add(warehouse);
            mapDistrictById(districtMap, warehouse);
          }
        }

        // Read the "employees" array
        // Central idea:
        // Employee objects will be read and instantiated to close the circular reference in Java
        // objects
        else if ("employees".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Employee employee = new Employee();

            // Read the employee properties
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
              String property = jsonParser.getCurrentName();

              if ("id".equals(property)) {
                employee.setId(jsonParser.nextTextValue());
              } else if ("firstName".equals(property)) {
                employee.setFirstName(jsonParser.nextTextValue());
              } else if ("middleName".equals(property)) {
                employee.setMiddleName(jsonParser.nextTextValue());
              } else if ("lastName".equals(property)) {
                employee.setLastName(jsonParser.nextTextValue());
              } else if ("address".equals(property)) {
                // Read the address object nested in employee object
                Address address = new Address();

                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                  String addressProperty = jsonParser.getCurrentName();

                  if ("street1".equals(addressProperty)) {
                    address.setStreet1(jsonParser.nextTextValue());
                  } else if ("street2".equals(addressProperty)) {
                    address.setStreet2(jsonParser.nextTextValue());
                  } else if ("zipCode".equals(addressProperty)) {
                    address.setZipCode(jsonParser.nextTextValue());
                  } else if ("city".equals(addressProperty)) {
                    address.setCity(jsonParser.nextTextValue());
                  } else if ("state".equals(addressProperty)) {
                    address.setState(jsonParser.nextTextValue());
                  }
                }

                employee.setAddress(address);
              } else if ("phoneNumber".equals(property)) {
                employee.setPhoneNumber(jsonParser.nextTextValue());
              } else if ("email".equals(property)) {
                employee.setEmail(jsonParser.nextTextValue());
              } else if ("title".equals(property)) {
                employee.setTitle(jsonParser.nextTextValue());
              } else if ("username".equals(property)) {
                employee.setUsername(jsonParser.nextTextValue());
              } else if ("password".equals(property)) {
                employee.setPassword(jsonParser.nextTextValue());
              } else if ("role".equals(property)) {
                employee.setRole(jsonParser.nextTextValue());
              } else if ("district".equals(property)) {
                String districtID = jsonParser.nextTextValue();
                District district = districtMap.get(districtID);

                if (district == null) {
                  // Fetch the district object from warehouses
                  district = fetchDistrictById(districtID, warehouses);
                  districtMap.put(districtID, district);
                }

                // Closing the circular reference of the data model by setting reference to district
                employee.setDistrict(district);
              }
            }

            employees.add(employee);
          }
        }

        // Read the "carriers" array
        else if ("carriers".equals(fieldName)) {
          jsonParser.nextToken();
          while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            Carrier carrier = objectMapper.readValue(jsonParser, Carrier.class);
            carriers.add(carrier);
          }
        }

        // Read the "stats" object
        else if ("stats".equals(fieldName)) {
          jsonParser.nextToken();
          stats = objectMapper.readValue(jsonParser, Stats.class);
        }
      }
    }

    stopwatch.stop();
    LOG.info("Deserialization took : {}", stopwatch.getDuration());

    return new DataGeneratorModel(products, warehouses, employees, carriers, stats);
  }

  public static District fetchDistrictById(String districtID, List<Warehouse> warehouses) {
    for (Warehouse warehouse : warehouses) {
      for (int k = 0; k < warehouse.getDistricts().size(); k++)
        if (Objects.equals(districtID, warehouse.getDistricts().get(k).getId())) {
          return warehouse.getDistricts().get(k);
        }
    }
    return null;
  }

  private void mapDistrictById(Map<String, District> districtMap, Warehouse warehouse) {
    List<District> districtsList = warehouse.getDistricts();
    for (District district : districtsList) {
      districtMap.put(district.getId(), district);
    }
  }
}

package de.uniba.dsg.wss.data.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.util.List;

/**
 * Class for storing the converted model data produced by {@link DataGenerator} implementations.
 *
 * @author Benedikt Full
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataGeneratorModel extends BaseDataModel<Product, Warehouse, Employee, Carrier> {

  @JsonCreator
  public DataGeneratorModel(
      @JsonProperty("products") List<Product> products,
      @JsonProperty("warehouses") List<Warehouse> warehouses,
      @JsonProperty("employees") List<Employee> employees,
      @JsonProperty("carriers") List<Carrier> carriers,
      @JsonProperty("stats") Stats stats) {
    super(products, warehouses, employees, carriers, stats);
  }
}

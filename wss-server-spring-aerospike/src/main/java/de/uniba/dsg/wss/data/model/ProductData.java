package de.uniba.dsg.wss.data.model;

import org.springframework.data.aerospike.mapping.Document;

/**
 * A product of the wholesale supplier can be retrieved via their unique identifier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 * @author Andre Maier
 */
@Document(collection = "Product")
public class ProductData extends BaseData {

  private final String imagePath;
  private final String name;
  private final double price;
  private final String data;

  public ProductData(String id, String imagePath, String name, double price, String data) {
    super(id);
    this.imagePath = imagePath;
    this.name = name;
    this.price = price;
    this.data = data;
  }

  public String getImagePath() {
    return imagePath;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public String getData() {
    return data;
  }
}

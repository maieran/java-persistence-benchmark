package de.uniba.dsg.wss.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Product")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ProductData extends BaseData implements Serializable {

  private final String imagePath;
  private final String name;
  private final double price;
  private final String data;

  @JsonCreator
  public ProductData(
      @JsonProperty("id") String id,
      @JsonProperty("imagePath") String imagePath,
      @JsonProperty("name") String name,
      @JsonProperty("price") double price,
      @JsonProperty("data") String data) {
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

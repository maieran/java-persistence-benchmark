package de.uniba.dsg.wss.data.gen.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.UUID;

/**
 * The base class for all model classes. It defines the identifier for the object, which is a UUID.
 *
 * @author Benedikt Full
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Customer.class, name = "customer"),
  @JsonSubTypes.Type(value = Employee.class, name = "employee"),
  @JsonSubTypes.Type(value = District.class, name = "district"),
  @JsonSubTypes.Type(value = Payment.class, name = "payment"),
  @JsonSubTypes.Type(value = OrderItem.class, name = "orderItem"),
  @JsonSubTypes.Type(value = Order.class, name = "order"),
  @JsonSubTypes.Type(value = Person.class, name = "person"),
  @JsonSubTypes.Type(value = Stock.class, name = "stock"),
  @JsonSubTypes.Type(value = Warehouse.class, name = "warehouse"),
  @JsonSubTypes.Type(value = Product.class, name = "product")
})
public abstract class Base {

  private String id;

  public Base() {
    id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}

package de.uniba.dsg.wss.data.model;

import java.util.UUID;
import org.springframework.data.annotation.Id;

public abstract class BaseData {
  @Id protected final String id;

  public BaseData() {
    id = UUID.randomUUID().toString();
  }

  public BaseData(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}

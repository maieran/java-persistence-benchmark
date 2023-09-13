package de.uniba.dsg.wss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aerospike")
public class AerospikeConfigurationProperties {

  private final Environment environment;

  private String host;
  private int port;
  private String namespace;

  @Autowired
  public AerospikeConfigurationProperties(Environment environment) {
    this.environment = environment;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = environment.getRequiredProperty("wss.aerospike.host");
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = Integer.parseInt(environment.getRequiredProperty("wss.aerospike.port"));
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = environment.getRequiredProperty("wss.aerospike.namespace");
  }
}

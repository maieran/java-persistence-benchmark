package de.uniba.dsg.wss.service;

import com.aerospike.client.Host;
import java.util.Collection;
import java.util.Collections;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;

@TestConfiguration
public class TestAerospikeConfiguration extends AbstractAerospikeDataConfiguration {

  private final Environment environment;

  public TestAerospikeConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Override
  @Bean
  protected Collection<Host> getHosts() {
    return Collections.singleton(
        new Host(
            environment.getRequiredProperty("wss.aerospike.host"),
            Integer.parseInt(environment.getRequiredProperty("wss.aerospike.port"))));
  }

  @Override
  @Bean
  protected String nameSpace() {
    return (environment.getRequiredProperty("wss.aerospike.namespace"));
  }

  @Bean
  public AerospikeDataSettings aerospikeDataSettings() {
    return AerospikeDataSettings.builder()
        .scansEnabled(true) // Enable scans
        .build();
  }
}

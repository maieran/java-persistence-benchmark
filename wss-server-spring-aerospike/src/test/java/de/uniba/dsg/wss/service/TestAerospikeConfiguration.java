package de.uniba.dsg.wss.service;

import com.aerospike.client.Host;
import java.util.Collection;
import java.util.Collections;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;

@TestConfiguration
public class TestAerospikeConfiguration extends AbstractAerospikeDataConfiguration {

  @Override
  @Bean
  protected Collection<Host> getHosts() {
    return Collections.singleton(new Host("localhost", 3000));
  }

  @Override
  @Bean
  protected String nameSpace() {
    return "test";
  }

  @Bean
  public AerospikeDataSettings aerospikeDataSettings() {
    return AerospikeDataSettings.builder()
        .scansEnabled(true) // Enable scans
        .build();
  }
}

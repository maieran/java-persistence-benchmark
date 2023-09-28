package de.uniba.dsg.wss;

import com.aerospike.client.Host;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

// @EnableTransactionManagement
@Configuration
// @EnableConfigurationProperties(AerospikeConfigurationProperties.class)
// @ComponentScan(basePackages = "de.uniba.dsg.wss")
// @EnableAerospikeRepositories // (basePackageClasses = { PersonRepository.class})
@EnableAerospikeRepositories(basePackages = {"de.uniba.dsg.wss.data.access"})
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

  @Autowired private AerospikeConfigurationProperties aerospikeConfigurationProperties;

  //  @Override
  //  protected Collection<Host> getHosts() {
  //    return Collections.singleton(
  //        new Host(
  //            aerospikeConfigurationProperties.getHost(),
  //            aerospikeConfigurationProperties.getPort()));
  //  }
  //
  //  @Override
  //  protected String nameSpace() {
  //    return aerospikeConfigurationProperties.getNamespace();
  //  }

  @Override
  protected Collection<Host> getHosts() {
    return Collections.singleton(new Host("localhost", 3000));
  }

  @Override
  protected String nameSpace() {
    return "test";
  }

  @Bean
  public AerospikeDataSettings aerospikeDataSettings() {
    return AerospikeDataSettings.builder()
        .scansEnabled(true) // Enable scans
        .build();
  }

  //   @Bean
  //  public AerospikeTemplate aerospikeTemplate() {
  //    return new AerospikeTemplate();
  //  }
}

package de.uniba.dsg.wss;

import com.aerospike.client.Host;
import java.util.Collection;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

/**
 * Configures the set-up for Aerospike data access in the corresponding repositories.
 * Facilitates the connection to the Aerospike database by defining the hosts and establishing
 * the namespace.
 * Provides the Aerospike Java client responsible for managing communication with
 * the underlying Aerospike server and its data model.
 * Provides also {@link AerospikeDataSettings} that enables scan functionality on secondary index that
 * is required in {@link de.uniba.dsg.wss.data.model.EmployeeData} for 'username' retrieve when accessing the terminal,
 * that is implemented in {@link de.uniba.dsg.wss.data.access.EmployeeRepositoryOperationsImpl}.
 *
 *
 * @author Andre Maier
 */
@Configuration
@EnableAerospikeRepositories(basePackages = {"de.uniba.dsg.wss.data.access"})
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

  @Override
  protected Collection<Host> getHosts() {
    return Collections.singleton(new Host("localhost", 3000));
  }

  @Override
  protected String nameSpace() {
    return "test";
  }

  /**
   * The {@link AerospikeDataSettings} object allows the fine-tuning of scan-related settings and other properties related to data access and manipulation.
   * Enable scan operations for accessing and retrieving data from the Aerospike database.
   *
   *
   * @return An instance of {@link AerospikeDataSettings} that encapsulates the configuration settings for Aerospike data operations.
   */
  @Bean
  public AerospikeDataSettings aerospikeDataSettings() {
    return AerospikeDataSettings.builder()
        .scansEnabled(true) // Enable scans
        .build();
  }
}

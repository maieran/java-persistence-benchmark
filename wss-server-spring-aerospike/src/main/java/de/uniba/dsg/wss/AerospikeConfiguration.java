package de.uniba.dsg.wss;

import com.aerospike.client.Host;
import java.util.Collection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAerospikeRepositories // (basePackageClasses = { PersonRepository.class})
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {
  @Override
  protected Collection<Host> getHosts() {
    return null;
  }

  @Override
  protected String nameSpace() {
    return null;
  }

  /*  @Override
  protected Collection<Host> getHosts() {
    return Collections.singleton(new Host("localhost", 3000));
  }

  @Override
  protected String nameSpace() {
    return "test";
  }*/
}

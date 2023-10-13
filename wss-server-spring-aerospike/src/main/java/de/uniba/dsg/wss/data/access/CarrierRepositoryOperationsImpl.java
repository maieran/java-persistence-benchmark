package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.CarrierData;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeOperations;
import org.springframework.data.aerospike.core.AerospikeTemplate;

public class CarrierRepositoryOperationsImpl implements CarrierRepositoryOperations {

  // private final AerospikeOperations aerospikeOperations;
  private final AerospikeTemplate aerospikeTemplate;

  @Autowired
  public CarrierRepositoryOperationsImpl(
      AerospikeOperations aerospikeOperations, AerospikeTemplate aerospikeTemplate) {
    // this.aerospikeOperations = aerospikeOperations;
    this.aerospikeTemplate = aerospikeTemplate;
  }

  @Override
  public void saveAll(Map<String, CarrierData> idsToCarriers) {
    WritePolicy writePolicy = new WritePolicy();
    writePolicy.sendKey = true;

    idsToCarriers.forEach((id, carrier) -> aerospikeTemplate.save(carrier));
  }

  @Override
  public Map<String, CarrierData> getCarriers() {
    return aerospikeTemplate
        .findAll(CarrierData.class)
        .collect(Collectors.toMap(CarrierData::getId, carrier -> carrier));
  }
}

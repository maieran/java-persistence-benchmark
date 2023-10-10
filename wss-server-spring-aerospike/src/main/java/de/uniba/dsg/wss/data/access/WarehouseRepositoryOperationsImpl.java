package de.uniba.dsg.wss.data.access;

import com.aerospike.client.policy.WritePolicy;
import de.uniba.dsg.wss.data.model.WarehouseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

import java.util.Map;

public class WarehouseRepositoryOperationsImpl implements WarehouseRepositoryOperations {

    private final AerospikeTemplate aerospikeTemplate;

    @Autowired
    public WarehouseRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
        this.aerospikeTemplate = aerospikeTemplate;
    }


    @Override
    public void saveAll(Map<String, WarehouseData> idsToWarehouse) {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;

        idsToWarehouse.forEach((id, warehouse) -> aerospikeTemplate.save(warehouse));
    }
}

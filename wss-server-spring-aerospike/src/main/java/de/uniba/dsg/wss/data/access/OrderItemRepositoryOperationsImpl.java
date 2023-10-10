package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aerospike.core.AerospikeTemplate;

import java.util.List;
import java.util.Map;

public class OrderItemRepositoryOperationsImpl implements OrderRepositoryOperations{

    private final AerospikeTemplate aerospikeTemplate;

    @Autowired
    public OrderItemRepositoryOperationsImpl(AerospikeTemplate aerospikeTemplate) {
        this.aerospikeTemplate = aerospikeTemplate;
    }


    @Override
    public void saveAll(Map<String, OrderData> idsToOrders) {

    }

    @Override
    public List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds) {
        return null;
    }
}

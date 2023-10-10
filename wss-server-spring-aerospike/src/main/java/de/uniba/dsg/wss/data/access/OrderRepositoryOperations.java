package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.OrderData;

import java.util.List;
import java.util.Map;

public interface OrderRepositoryOperations {
    void saveAll(Map<String, OrderData> idsToOrders);

    List<OrderData> getOrdersFromDistrict(List<String> orderRefsIds);
}

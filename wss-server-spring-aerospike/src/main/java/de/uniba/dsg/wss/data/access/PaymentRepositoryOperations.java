package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;

import java.util.Map;

public interface PaymentRepositoryOperations {
    void saveAll(Map<String, PaymentData> idsToPayments);
}

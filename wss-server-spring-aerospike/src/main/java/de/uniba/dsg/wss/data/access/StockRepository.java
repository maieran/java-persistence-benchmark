package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(readOnly = true)
public interface StockRepository extends AerospikeRepository<StockData, Integer> {
    <S extends StockData> Iterable<S> saveAll(Map<String, StockData> idsToStocks);


}

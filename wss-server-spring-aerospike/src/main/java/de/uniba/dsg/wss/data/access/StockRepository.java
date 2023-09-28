package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.StockData;
import java.util.Map;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StockRepository extends AerospikeRepository<StockData, Integer> {
  <S extends StockData> Iterable<S> saveAll(Map<String, StockData> idsToStocks);
}

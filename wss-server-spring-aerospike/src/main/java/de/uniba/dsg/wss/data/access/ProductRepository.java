package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.model.ProductData;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.Map;

public interface ProductRepository extends AerospikeRepository<ProductData, Integer> {
    <S extends ProductData> Iterable<S> saveAll(Map<String, ProductData> idsToProducts);

    @Override
    Iterable<ProductData> findAll();
}

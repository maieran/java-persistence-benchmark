package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the repository interface for accessing and modifying {@link PaymentData
 * payments}.
 *
 * @author Andre Maier
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
  private static final int BATCH_SIZE = 2000;
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, PaymentData> hashOperations;

  public PaymentRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  /*  @Override
  public void saveAll(Map<String, PaymentData> payments) {
    String hashKey = "payments";
    hashOperations.putAll(hashKey, payments);
  }*/

  @Override
  public void saveAll(Map<String, PaymentData> payments) {
    String hashKey = "payments";
    BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(hashKey);

    int offset = 0;
    while (offset < payments.size()) {
      int endIndex = Math.min(offset + BATCH_SIZE, payments.size());
      Map<String, PaymentData> batch = getBatch(payments, offset, endIndex);
      boundHashOps.putAll(batch);
      offset += BATCH_SIZE;
    }
  }

  private Map<String, PaymentData> getBatch(Map<String, PaymentData> payments, int start, int end) {
    return payments.entrySet().stream()
        .skip(start)
        .limit(end - start)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public void save(PaymentData payment) {
    String hashKey = "payments";
    hashOperations.put(hashKey, payment.getId(), payment);
  }

  @Override
  public Map<String, PaymentData> getPayments() {
    String hashKey = "payments";
    return hashOperations.entries(hashKey);
  }

  @Override
  public void deleteAll() {
    String hashKey = "payments";
    redisTemplate.delete(hashKey);
  }
}

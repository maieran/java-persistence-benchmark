package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentData;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

  private final HashOperations<String, String, PaymentData> hashOperations;

  public PaymentRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveAll(Map<String, PaymentData> payments) {
    String hashKey = "payments";
    hashOperations.putAll(hashKey, payments);
  }

  @Override
  public void save(PaymentData payment) {
    String hashKey = "payments";
    hashOperations.put(hashKey, payment.getId(), payment);
  }
}

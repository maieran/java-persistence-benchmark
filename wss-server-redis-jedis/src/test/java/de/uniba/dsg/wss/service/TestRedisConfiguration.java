package de.uniba.dsg.wss.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@TestConfiguration
// @AutoConfigureBefore(RedisConfiguration.class) // Exclude the production Redis configuration
public class TestRedisConfiguration {

  private final Environment environment;

  @Autowired
  public TestRedisConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public JedisPoolConfig jedisPoolConfig() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(
        Integer.parseInt(environment.getRequiredProperty("wss.redis.jedis.pool.max-total")));
    poolConfig.setMaxIdle(
        Integer.parseInt(environment.getRequiredProperty("wss.redis.jedis.pool.max-idle")));
    poolConfig.setMinIdle(
        Integer.parseInt(environment.getRequiredProperty("wss.redis.jedis.pool.min-idle")));
    poolConfig.setMaxWaitMillis(
        Long.parseLong(environment.getRequiredProperty("spring.redis.timeout")));
    return poolConfig;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(environment.getRequiredProperty("spring.redis.host"));
    redisStandaloneConfiguration.setPort(
        Integer.parseInt(environment.getRequiredProperty("spring.redis.port")));

    JedisClientConfiguration jedisClientConfiguration =
        getJedisClientConfiguration(jedisPoolConfig());

    return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
  }

  @Bean
  public JedisClientConfiguration getJedisClientConfiguration(JedisPoolConfig poolConfig) {
    return JedisClientConfiguration.builder()
        .usePooling()
        .poolConfig(poolConfig)
        .and()
        .readTimeout(
            Duration.ofMillis(
                Integer.parseInt(environment.getRequiredProperty("spring.redis.timeout"))))
        .build();
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());

    // Set the serializer for keys
    redisTemplate.setKeySerializer(new StringRedisSerializer());

    // Set the serializer for values
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    PolymorphicTypeValidator validator =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();

    objectMapper.activateDefaultTyping(
        validator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

    GenericJackson2JsonRedisSerializer valueSerializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    redisTemplate.setDefaultSerializer(valueSerializer);
    redisTemplate.setValueSerializer(valueSerializer);
    redisTemplate.setHashValueSerializer(valueSerializer);

    return redisTemplate;
  }
}

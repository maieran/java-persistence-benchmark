package de.uniba.dsg.wss;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableTransactionManagement
public class RedisConfiguration {

  private static final Logger LOG = LogManager.getLogger(RedisConfiguration.class);

  private final Environment environment;

  @Autowired
  public RedisConfiguration(Environment environment) {
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
    return poolConfig;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(
        environment.getRequiredProperty("wss.redis.jedis.host"));
    redisStandaloneConfiguration.setPort(6379);
    return new JedisConnectionFactory(redisStandaloneConfiguration);
  }

  // Performs connection and communication with redis server and handles the defined data model
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {

    /* Uses a String for object IDs and Object as the base class for serialization and deserialization
    of different data model objects, utilizing a polymorphic approach with the help of the Jackson
    Library and GenericJackson2JsonRedisSerializer to access data via JSON. */
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());

    // Set the serializer for keys, which are string-type and represents the ids' of our objects
    redisTemplate.setKeySerializer(new StringRedisSerializer());

    // Set the serializer for values
    ObjectMapper objectMapper = new ObjectMapper();
    // Registers the JavaTimeModule to assist with serialization/deserialization of LocalDateTime
    // objects
    objectMapper.registerModule(new JavaTimeModule());

    /* Validates and controls serialization to JSON and deserialization from Java Objects of polymorphic types
    of classes, which are marked with @JsonTypeInfo and operates in combination with object mapper */
    PolymorphicTypeValidator validator =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();

    objectMapper.activateDefaultTyping(
        validator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

    GenericJackson2JsonRedisSerializer valueSerializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    // Configuring the serializer
    redisTemplate.setDefaultSerializer(valueSerializer);
    redisTemplate.setValueSerializer(valueSerializer);
    redisTemplate.setHashValueSerializer(valueSerializer);

    return redisTemplate;
  }
}

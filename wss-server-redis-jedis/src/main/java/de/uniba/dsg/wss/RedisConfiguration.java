package de.uniba.dsg.wss;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Configures the connection and communication with the Redis server as well as handling of the
 * defined data model in {@link de.uniba.dsg.wss.data.gen.DataModel}. Moreover, it provides bean for
 * {@link JedisPoolConfig} and {@link RedisTemplate}, which enables interaction with Redis through
 * JSON-based serialization and deserialization.
 *
 * @author Andre Maier
 */
@Configuration
// @EnableRedisRepositories(basePackages = "de.uniba.dsg.wss.data.access")
@EnableRedisRepositories
public class RedisConfiguration {

  private final Environment environment;

  @Autowired
  public RedisConfiguration(Environment environment) {
    this.environment = environment;
  }

  /**
   * Implements and configures the JedisPool that is responsible for connection with Redis. All
   * configuration are fetched from application environment.
   *
   * @return the JedisPool bean with configured pool properties for the interaction.
   */
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
        Integer.parseInt(environment.getRequiredProperty("spring.redis.timeout")));
    return poolConfig;
  }

  /**
   * Provides the connection to the Redis server by setting the host and port of the Redis server.
   *
   * @return the configured connection to the Redis server.
   */
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

  /**
   * Facilitates the interaction with Redis server and enables JSON-based serialization by setting
   * the necessary serializers with Jackson library for polymorphic type handling during the
   * serialization process.
   *
   * @return redisTemplate bean with the configured JSON-based serialization and deserialization
   */
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

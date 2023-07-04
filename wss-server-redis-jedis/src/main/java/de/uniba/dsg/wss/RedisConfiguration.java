package de.uniba.dsg.wss;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  // TODO: Step 0
  // Make the host and port available from resources/properties to redis client
  @Value("${wss.redis.jedis.host}")
  private String host;

  @Value("${server.port}")
  private int port;

  // TODO: Step 1.
  // Configuration of JedisPool from application properties
  // Especially needed in production, since we expect concurrent/multi-threaded requests
  // TODO: Ongoing Task - Include over environment variable
  // Read: https://docs.jdcloud.com/en/jcs-for-redis/jedispool-connct
  @Value("${wss.redis.jedis.pool.max-total}")
  private int maxTotal;

  @Value("${wss.redis.jedis.pool.max-idle}")
  private int maxIdle;

  @Value("${wss.redis.jedis.pool.min-idle}")
  private int minIdle;

  // TODO: Step 2.
  // Creation of JedisConnection Factory with JedisPool client configuration for multi-threaded
  // purposes
  // Using apache's GenericObjectPoolConfig which is extended by
  // JedisPoolingClientConfigurationBuilder to configure the pool

  // final JedisPoolConfig poolConfig = buildPoolConfig();

  @Bean
  public JedisPoolConfig jedisPoolConfig() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(maxTotal);
    poolConfig.setMaxIdle(maxIdle);
    poolConfig.setMinIdle(minIdle);
    return poolConfig;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(6379);
    return new JedisConnectionFactory(redisStandaloneConfiguration);
  }

  // TODO: BIG TOPIC , NEED TO CONFIGURE THE MOST EFFICIENT AND EFFECTIVE SERIALIZERS

  /**
   * On how to store raw json into redis :
   * https://stackoverflow.com/questions/41875635/storing-raw-json-in-redis-by-using-spring-data-redis
   * On mapping of polymorphic models and class when using objectMapper :
   * https://stackoverflow.com/questions/19239413/de-serializing-json-to-polymorphic-object-model-using-spring-and-jsontypeinfo-an
   * On how to set up the polymorphic de-/serializer :
   * https://stackoverflow.com/questions/63339255/deserialize-list-of-polymorphic-objects-into-object-field
   *
   * @return
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());

    // Set the serializer for keys
    redisTemplate.setKeySerializer(new StringRedisSerializer());

    // Set the serializer for values
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    // objectMapper.registerModule(new SimpleModule().addDeserializer(DistrictData.class, new
    // DistrictDataDeserializer()));

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

  //  @Bean
  //  public PolymorphicTypeValidator customPolymorphicTypeValidator() {
  //    return BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();
  //  }

  //  @Bean
  //  public RedisTemplate<String, Object> redisTemplate() {
  //    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
  //    redisTemplate.setConnectionFactory(jedisConnectionFactory());
  //
  //    // Set the serializer for keys
  //    redisTemplate.setKeySerializer(new StringRedisSerializer());
  //
  //    // Set the serializer for values
  //    ObjectMapper objectMapper = new ObjectMapper();
  //    objectMapper.registerModule(new JavaTimeModule());
  //
  //    Jackson2JsonRedisSerializer<Object> valueSerializer =
  //            new Jackson2JsonRedisSerializer<>(Object.class);
  //    valueSerializer.setObjectMapper(objectMapper);
  //
  //    redisTemplate.setValueSerializer(valueSerializer);
  //
  //    // Set the serializer for hash keys (if needed, otherwise comment out and use the default)
  //    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
  //
  //    // Set the serializer for hash values (if needed, otherwise comment out and use the default)
  //    redisTemplate.setHashValueSerializer(valueSerializer);
  //
  //
  //    return redisTemplate;
  //  }

  /////////////////
  //  @Bean
  //  public RedisTemplate<String, Object> redisTemplate() {
  //    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
  //    redisTemplate.setConnectionFactory(jedisConnectionFactory());
  //
  //    // Set the serializer for keys
  //    redisTemplate.setKeySerializer(new StringRedisSerializer());
  //
  //    // Set the serializer for values
  //    Jackson2JsonRedisSerializer<Object> valueSerializer =
  //        new Jackson2JsonRedisSerializer<>(Object.class);
  //    redisTemplate.setValueSerializer(valueSerializer);
  //
  //    // Set the serializer for hash keys (if needed, otherwise comment out and use the default)
  //    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
  //
  //    // Set the serializer for hash values (if needed, otherwise comment out and use the default)
  //    redisTemplate.setHashValueSerializer(valueSerializer);
  //
  //    return redisTemplate;
  //  }

}

package com.hotlist.config;

import com.alibaba.fastjson2.support.spring.data.redis.FastJsonRedisSerializer;
import io.lettuce.core.ReadFrom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class RedisConfig {

    private final String HOST;

    private final int PORT;

    private final int DATABASE;

    private final String PASSWORD;

    public RedisConfig(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.database}") int database,
            @Value("${spring.redis.password}") String password
//            @Value("${redis.timeout}") long timeout
    ) {

        this.HOST = host;
        this.PORT = port;
        this.DATABASE = database;
        this.PASSWORD = password;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory,
                                                       RedisSerializer<Object> redisSerializer
    ) {
        RedisTemplate<String, Object> objectObjectRedisTemplate = new RedisTemplate<>();
        objectObjectRedisTemplate.setConnectionFactory(factory);
        objectObjectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectObjectRedisTemplate.setValueSerializer(redisSerializer);

        objectObjectRedisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        objectObjectRedisTemplate.setHashValueSerializer(redisSerializer);
        return objectObjectRedisTemplate;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new FastJsonRedisSerializer<>(Object.class);
    }

    /**
     * 读写分离配置
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(1000L))
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();


        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(HOST);
        config.setPort(PORT);
        config.setDatabase(DATABASE);
        config.setPassword(PASSWORD);
        return new LettuceConnectionFactory(config, clientConfig);
    }
}

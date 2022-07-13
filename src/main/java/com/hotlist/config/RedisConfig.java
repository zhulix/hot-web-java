package com.hotlist.config;

import com.alibaba.fastjson2.support.spring.data.redis.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new FastJsonRedisSerializer<>(Object.class);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, RedisSerializer<Object> redisSerializer) {
        RedisTemplate<String, Object> objectObjectRedisTemplate = new RedisTemplate<>();

        objectObjectRedisTemplate.setConnectionFactory(factory);

//        objectObjectRedisTemplate.setKeySerializer(new GenericToStringSerializer<>(Object.class));
        objectObjectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectObjectRedisTemplate.setValueSerializer(redisSerializer);

        objectObjectRedisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        objectObjectRedisTemplate.setHashValueSerializer(redisSerializer);
        return objectObjectRedisTemplate;
    }
}

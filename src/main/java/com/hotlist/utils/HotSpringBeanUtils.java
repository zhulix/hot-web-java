package com.hotlist.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HotSpringBeanUtils implements ApplicationContextAware {

    public static RedisTemplate<String, Object> redisTemplate;

    public static StringRedisTemplate stringRedisTemplate;

    public HotSpringBeanUtils(RedisTemplate<String, Object> redisTemplate,
                              StringRedisTemplate stringRedisTemplate) {
        HotSpringBeanUtils.redisTemplate = redisTemplate;
        HotSpringBeanUtils.stringRedisTemplate = stringRedisTemplate;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }


    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String clazz) {
        try {
            return applicationContext.getBean(Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        HotSpringBeanUtils.applicationContext = applicationContext;
    }
}

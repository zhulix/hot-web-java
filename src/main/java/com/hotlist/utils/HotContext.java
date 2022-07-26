package com.hotlist.utils;

import com.alibaba.fastjson2.JSON;
import com.hotlist.entity.UserEntity;

public class HotContext {

    private static final ThreadLocal<UserEntity> USER_ENTITY_THREAD_LOCAL = new ThreadLocal<>();

    public static UserEntity getCurrentUser() {
        return USER_ENTITY_THREAD_LOCAL.get();
    }

    public static void setUser(UserEntity user) {
        USER_ENTITY_THREAD_LOCAL.set(user);
    }

    public static void setDefaultUser() {
        Object xin = HotSpringBeanUtils.redisTemplate.opsForHash().get("hot:user", "xin");
        USER_ENTITY_THREAD_LOCAL.set(JSON.to(UserEntity.class, xin));
    }

    public static void removeUser() {
        USER_ENTITY_THREAD_LOCAL.remove();
    }

}

package com.hotlist.utils;

import com.hotlist.entity.UserEntity;

public class HotContext {

//    UserEntity user;

    private static final ThreadLocal<UserEntity> USER_ENTITY_THREAD_LOCAL = new ThreadLocal<>();

    public static UserEntity getCurrentUser() {
        return USER_ENTITY_THREAD_LOCAL.get();
    }

    public static void setUser(UserEntity user) {
        USER_ENTITY_THREAD_LOCAL.set(user);
    }

}

package com.hotlist.dao.impl;

import com.alibaba.fastjson2.JSON;
import com.hotlist.dao.UserDAO;
import com.hotlist.entity.UserEntity;
import com.hotlist.utils.HotUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class UserDAOImpl implements UserDAO {

    public static final String USER_PREFIX = HotUtil.stringJoin("hot", "user");

    private final RedisTemplate<String, Object> redisTemplate;

    public UserDAOImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean saveUser(UserEntity user) {
        String encode = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encode);
        return redisTemplate.opsForHash().putIfAbsent(USER_PREFIX, user.getUserName(), user);
    }

    @Override
    public UserEntity findByUserName(String userName) {
        Object o = redisTemplate.opsForHash().get(USER_PREFIX, userName);
        return Objects.isNull(o) ? null : JSON.to(UserEntity.class, o);
    }

    @Override
    public void updateUser(UserEntity user) {
        redisTemplate.opsForHash().put(USER_PREFIX, user.getUserName(), user);
    }

}

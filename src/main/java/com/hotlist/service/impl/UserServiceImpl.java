package com.hotlist.service.impl;

import com.alibaba.fastjson2.JSON;
import com.hotlist.common.R;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean save(UserEntity user) {
        String encode = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encode);
        return redisTemplate.opsForHash().putIfAbsent(user.getKey(), user.getUserName(), user);
    }

    @Override
    public UserEntity selectByUserName(String userName) {
        Object o = redisTemplate.opsForHash().get(new UserEntity().getKey(), userName);
        return Objects.isNull(o) ? null : JSON.to(UserEntity.class, o);
    }

    @Override
    public void update(UserEntity user) {
        redisTemplate.opsForHash().put(user.getKey(), user.getUserName(), user);
    }
}

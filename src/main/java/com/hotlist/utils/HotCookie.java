package com.hotlist.utils;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hotlist.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class HotCookie {

    @Value("${token.privateKey}")
    private String privateKey;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String createToken(UserEntity user) {
        return JWT.create().withClaim("username", user.getUserName()).withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)).sign(Algorithm.HMAC256(privateKey));
    }

    public void validate(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(privateKey))
                .build().verify(token);
        String username = verify.getClaim("username").asString();
        Object user = stringRedisTemplate.opsForHash().get(HotUtil.stringJoin("hot", "user"), username);
        UserEntity to = JSON.to(UserEntity.class, user);
        HotContext.setUser(to);
    }



}

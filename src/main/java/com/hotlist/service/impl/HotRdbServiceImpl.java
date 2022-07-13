package com.hotlist.service.impl;

import com.alibaba.fastjson2.JSON;
import com.hotlist.common.dto.HotSiteDto;
import com.hotlist.common.dto.PostWrapper;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.HotRdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@Slf4j
@Service
public class HotRdbServiceImpl implements HotRdbService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Deprecated
    @Override
    public void saveByKey(String key, String value) {

    }

    @Deprecated
    @Override
    public void savePost(PostWrapper postWrapper) {
        // 解析方式存放
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set(postWrapper.getParseTypeKey(), postWrapper.getParseType());
        // url存放
        opsForValue.set(postWrapper.getUrlKey(), postWrapper.getUrl());
        // 数组键
        opsForValue.set(postWrapper.getArrayKey(), postWrapper.getArrayParseKey());

        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        // header存放
        opsForHash.putAll(postWrapper.getHeaderKey(), postWrapper.getHeader());
        // 解析格式
        opsForHash.putAll(postWrapper.getParseContentKey(), postWrapper.getParseContent());

        HotSiteEntity hotSite = new HotSiteEntity();

        redisTemplate.opsForValue().set(hotSite.getSaveKey(), hotSite);
    }

    @Override
    public void savePost(HotSiteDto hotSiteDto) {
        HotSiteEntity hotSite = new HotSiteEntity();
        BeanUtils.copyProperties(hotSiteDto, hotSite);
        hotSite.saveBySite();
//        redisTemplate.opsForValue().set(hotSite.getSaveKey(), hotSite);
    }

    @Override
    public HotSiteEntity getConfigByKey(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        Assert.notNull(o, "查询为空");
        return JSON.to(HotSiteEntity.class, o);
    }

    @Override
    public Object getResourceByKey(String key) {
//        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        return redisTemplate.opsForValue().get(key);
    }

}

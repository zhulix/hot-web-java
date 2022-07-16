package com.hotlist.service.impl;

import com.alibaba.fastjson2.JSON;
import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.HotResourceService;
import com.hotlist.service.ManagerService;
import com.hotlist.utils.HotContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Resource
    private HotResourceService hotResourceService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<HotSiteEntity> getMySite() {
        String siteHashKey = HotSiteEntity.getSiteHashKeyByCurrentUser();
        Set<Object> keys = redisTemplate.opsForHash().keys(siteHashKey);
        if (!keys.isEmpty()){
            return Objects.requireNonNull(redisTemplate.opsForHash().multiGet(siteHashKey, keys)).stream().map(k -> JSON.to(HotSiteEntity.class, k)).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public List<HotCardSiteWrapperVo> getMySiteResourceCard() {
        // 没有就默认放一个
        if (Objects.isNull(HotContext.getCurrentUser())) HotContext.setDefaultUser();

        List<HotSiteEntity> mySite = getMySite();
        if (CollectionUtils.isEmpty(mySite)) return null;

        mySite = mySite.stream().filter(HotSiteEntity::getAvailable).collect(Collectors.toList());
        return hotResourceService.getResourceByHotSites(mySite);
    }
}

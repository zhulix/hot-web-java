package com.hotlist.service.impl;

import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.core.HotResource;
import com.hotlist.core.HotSiteInfoWrapper;
import com.hotlist.core.filter.HotResultWrapper;
import com.hotlist.dao.impl.ResourceDAOImpl;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.HotResourceService;
import com.hotlist.service.ResourceService;
import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotRabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class HotResourceServiceImpl implements HotResourceService {

    private final ResourceService resourceService;

    private final StringRedisTemplate stringRedisTemplate;

    public HotResourceServiceImpl(ResourceService resourceService,
                                  StringRedisTemplate stringRedisTemplate) {
        this.resourceService = resourceService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<HotCardSiteWrapperVo> getResourceByHotSites(List<HotSiteEntity> mySites, UserEntity user) {
        List<HotCardSiteWrapperVo> result = new ArrayList<>(mySites.size());
        for (HotSiteEntity site : mySites) {

            try {
                HotCardSiteWrapperVo hotCardSiteWrapperVo = new HotCardSiteWrapperVo();
                // 原始资源list
                List<Map<String, String>> resourceByHotSite = getResourceByHotSite(site, user);
                hotCardSiteWrapperVo.setResultSite(resourceByHotSite);
                hotCardSiteWrapperVo.setSort(1);
                hotCardSiteWrapperVo.setSiteName(site.getSiteName());
                hotCardSiteWrapperVo.setSiteUrl(site.getUrl());
                hotCardSiteWrapperVo.setPageMappingConfig(site.getShowConfig().getPageMappingConfig());
                result.add(hotCardSiteWrapperVo);

            } catch (Exception e) {
                e.printStackTrace();
                log.info("站点[{}]，解析失败", site.getSiteName());
            }
        }
        return result;
    }

    @Retryable(maxAttempts = 5,
            value = {RuntimeException.class},
            backoff = @Backoff(delay = 2000L, multiplier = 1.5)
    )
    @Override
    public void refreshResource(HotSiteEntity site) {
        HotSiteInfoWrapper wrapper = new HotSiteInfoWrapper(site);
        HotResource parserBeanObject = site.getParserBeanObject();
        try {
            HotResultWrapper resultWrapper = parserBeanObject.fetch(wrapper);
            parserBeanObject.save(resultWrapper, HotContext.getCurrentUser());
        } catch (Exception e) {
            log.error("重试中...: {}", site.getSaveKey());
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void refreshResourceCallback(RuntimeException e, HotSiteEntity site) {
        log.error("站点重试请求失败：{}", site.getSaveKey());
        throw e;
    }

    public List<Map<String, String>> getResourceByHotSite(HotSiteEntity hotSite, UserEntity user) {
        String refresh = stringRedisTemplate.opsForValue().get(ResourceDAOImpl.RedisKey.resourceRefreshKey(hotSite, user));
        if (Objects.isNull(refresh)) {
            // refresh未获取到：表示未在10分种内刷新这个站点资源。这时发消息给mq更新即可
            // 在这里初次访问网页完成初始化
            HotRabbitUtils.sendRefreshResourceNow(new MessageSiteWrapper(hotSite, UUID.randomUUID().toString()));
        }
        return resourceService.findResourceByUser(hotSite, user);
    }

}

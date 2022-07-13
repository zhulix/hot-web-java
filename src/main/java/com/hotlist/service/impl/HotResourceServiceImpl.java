package com.hotlist.service.impl;

import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.HotRdbService;
import com.hotlist.service.HotResourceService;
import com.hotlist.core.HotSiteInfoWrapper;
import com.hotlist.core.HotResource;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotResourceServiceImpl implements HotResourceService {

    @Resource
    private HotRdbService hotRdbService;
    @Override
    public List<Object> getResourceByKey(String key) {
        List<Object> obj = getResCacheByKey(key);
        if (obj != null) return obj;

        HotSiteEntity hotSite = hotRdbService.getConfigByKey(key);
        HotResource hotResource = hotSite.getParserBeanObject();
        HotSiteInfoWrapper wrapper = new HotSiteInfoWrapper(hotSite);
        return hotResource.fetch(wrapper);
    }

    private List<Object> getResCacheByKey(String key) {
        return (List<Object>) hotRdbService.getResourceByKey(key);
    }

    @Override
    public List<HotCardSiteWrapperVo> getResourceByHotSites(List<HotSiteEntity> mySites) {
        List<HotCardSiteWrapperVo> result = new ArrayList<>(mySites.size());
        for (HotSiteEntity site : mySites) {

            try {
                HotCardSiteWrapperVo hotCardSiteWrapperVo = new HotCardSiteWrapperVo();
                // 原始资源list
                List<Map<String, String>> resourceByHotSite = getResourceByHotSite(site);
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

    public List<Map<String, String>> getResourceByHotSite(HotSiteEntity hotSite) {
        List<Map<String, String>> obj = hotSite.getResource();
        if (obj != null) return obj;
        HotResource hotResource = hotSite.getParserBeanObject();
        HotSiteInfoWrapper wrapper = new HotSiteInfoWrapper(hotSite);
        List<Object> fetch = hotResource.fetch(wrapper);
        return fetch.stream().map(o -> (Map<String, String>) o).collect(Collectors.toList());
    }

}

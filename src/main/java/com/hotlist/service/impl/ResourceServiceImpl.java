package com.hotlist.service.impl;

import com.hotlist.dao.ResourceDAO;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.ResourceService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceDAO resourceDAO;

    public ResourceServiceImpl(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }

    @Override
    public void saveResource(List<Object> resourceList, HotSiteEntity hotSite, UserEntity user) {
        resourceDAO.saveResource(resourceList, hotSite, user);
    }

    @Override
    public List<Map<String, String>> findResourceByUser(HotSiteEntity hotSite, UserEntity user) {
        return resourceDAO.findResourceByUser(hotSite, user);
    }

}

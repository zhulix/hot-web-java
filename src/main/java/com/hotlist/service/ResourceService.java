package com.hotlist.service;

import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface ResourceService {

    void saveResource(List<Object> resourceList, HotSiteEntity hotSite, UserEntity user);

    List<Map<String, String>> findResourceByUser(HotSiteEntity hotSite, UserEntity user);
}

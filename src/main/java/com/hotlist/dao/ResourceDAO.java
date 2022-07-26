package com.hotlist.dao;

import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface ResourceDAO {

    List<Map<String, String>> findResourceByUser(HotSiteEntity hotSite, UserEntity user);

    void saveResource(List<Object> resourceList, HotSiteEntity hotSite, UserEntity user);
}

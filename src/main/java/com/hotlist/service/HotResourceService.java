package com.hotlist.service;

import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.entity.HotSiteEntity;

import java.util.List;

public interface HotResourceService {
    Object getResourceByKey(String key);

    List<HotCardSiteWrapperVo> getResourceByHotSites(List<HotSiteEntity> mySites);

    void refreshResource(HotSiteEntity site);
}

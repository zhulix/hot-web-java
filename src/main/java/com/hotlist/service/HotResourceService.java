package com.hotlist.service;

import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;

import java.util.List;

public interface HotResourceService {

    List<HotCardSiteWrapperVo> getResourceByHotSites(List<HotSiteEntity> mySites, UserEntity user);

    void refreshResource(HotSiteEntity site);
}

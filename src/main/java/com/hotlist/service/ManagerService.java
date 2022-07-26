package com.hotlist.service;

import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;

import java.util.List;

public interface ManagerService {


    List<HotSiteEntity> getMySite(UserEntity user);

    List<HotCardSiteWrapperVo> getMySiteResourceCard(UserEntity user);
}

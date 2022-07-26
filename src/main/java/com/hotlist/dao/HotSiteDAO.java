package com.hotlist.dao;

import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;

import java.util.List;

public interface HotSiteDAO {

    List<HotSiteEntity> findSiteByUser(UserEntity user);

    void saveSite(HotSiteEntity site, UserEntity user);

    void delSite(HotSiteEntity site, UserEntity user);
}

package com.hotlist.service.impl;

import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.dao.HotSiteDAO;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.HotResourceService;
import com.hotlist.service.ManagerService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements ManagerService {


    public ManagerServiceImpl(HotSiteDAO hotSiteDAO,
                              HotResourceService hotResourceService
    ) {
        this.hotSiteDAO = hotSiteDAO;
        this.hotResourceService = hotResourceService;
    }

    private final HotSiteDAO hotSiteDAO;

    private HotResourceService hotResourceService;

    @Override
    public List<HotSiteEntity> getMySite(UserEntity user) {
        return hotSiteDAO.findSiteByUser(user);
    }

    @Override
    public List<HotCardSiteWrapperVo> getMySiteResourceCard(UserEntity user) {
        List<HotSiteEntity> mySite = getMySite(user);
        if (CollectionUtils.isEmpty(mySite)) return null;

        mySite = mySite.stream().filter(HotSiteEntity::getAvailable).collect(Collectors.toList());
        return hotResourceService.getResourceByHotSites(mySite, user);
    }
}

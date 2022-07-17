package com.hotlist.core.schedule;

import com.hotlist.common.vo.HotCardSiteWrapperVo;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.HotResourceService;
import com.hotlist.service.ManagerService;
import com.hotlist.utils.HotSpringBeanUtils;
import com.hotlist.utils.HotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
@EnableScheduling
public class ElasticSearchUpload {

    private ManagerService managerService;

    @Autowired
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void doUpload() {
        log.info("es job");
//        List<HotSiteEntity> mySite = managerService.getMySite();
//        for (HotSiteEntity site : mySite) {
//            String resourceObjKey = HotSiteEntity.resourceObjKey(site);
//            BoundListOperations<String, Object> listOps = HotSpringBeanUtils.redisTemplate.boundListOps(resourceObjKey);
//            List<Object> resList = listOps.range(0, -1);
//            List<Object> collect = resList.stream().map(res -> {
//
//            }).collect(Collectors.toList());
//
//        }
    }


}


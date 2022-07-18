package com.hotlist.core.schedule;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.hotlist.config.ElasticSearchConfig;
import com.hotlist.entity.HotResourceESModel;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.ManagerService;
import com.hotlist.utils.HotSpringBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Resource
    private ElasticsearchClient client;

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    @SuppressWarnings("unchecked")
    public void doUpload() {
        log.info("es job running...");
        List<HotSiteEntity> mySite = managerService.getMySite();
        List<HotResourceESModel> esModels = new ArrayList<>(256);
        for (HotSiteEntity site : mySite) {
            String resourceObjKey = HotSiteEntity.resourceObjKey(site);
            BoundListOperations<String, Object> listOps = HotSpringBeanUtils.redisTemplate.boundListOps(resourceObjKey);
            List<Object> resList = listOps.range(0, -1);

            assert resList != null;
            List<HotResourceESModel> collect = resList.stream().map(r -> {
                Map<String, String> res = (Map<String, String>) r;
                HotResourceESModel model = new HotResourceESModel();

                long timeStamp = Long.parseLong(res.getOrDefault("timeStamp", "1658029945000"));
                Instant instant = Instant.ofEpochMilli(timeStamp);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String format = formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
                model.setAddress(res.get("address")).setTitle(res.get("title"))
                        .setTimeStamp(format)
                        .setSiteName(site.getSiteName())
                        .setHotRankList(site.getHotRankList());
                return model;
            }).collect(Collectors.toList());
            esModels.addAll(collect);
            try {
                saveByElasticSearch(esModels);
                removeTodayRedisData(mySite);
            } catch (IOException e) {
                log.error("es上载失败");
                HotSpringBeanUtils.redisTemplate.opsForList().leftPushAll("es:upload", "error", esModels.toArray());
            }
        }
        log.info("es job done...");
    }

    public void saveByElasticSearch(List<HotResourceESModel> esModels) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (HotResourceESModel esModel : esModels) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(ElasticSearchConfig.INDEXES)
                            .id(esModel.getUid())
                            .document(esModel)
                    )
            );
        }
        BulkResponse result = client.bulk(br.build());
        if (result.errors()) {
            log.info("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.info(item.error().reason());
                }
            }
        }
    }

    public void removeTodayRedisData(List<HotSiteEntity> mySite) {
        for (HotSiteEntity site : mySite) {
            String resourceObjKey = HotSiteEntity.resourceObjKey(site);
            BoundListOperations<String, Object> listOps = HotSpringBeanUtils.redisTemplate.boundListOps(resourceObjKey);
            // 默认保留50条数据
            listOps.trim(0, 49);
        }
    }
}


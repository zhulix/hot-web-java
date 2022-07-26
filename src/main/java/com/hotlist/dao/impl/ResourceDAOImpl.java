package com.hotlist.dao.impl;

import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.dao.ResourceDAO;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.utils.HotRabbitUtils;
import com.hotlist.utils.HotSpringBeanUtils;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ResourceDAOImpl implements ResourceDAO {


    private final RedisTemplate<String, Object> redisTemplate;

    public ResourceDAOImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static class RedisKey {
        public static String resourceListKey(HotSiteEntity site, UserEntity user) {
            return HotSiteDAOImpl.RedisKey.resourceListKey(site, user);
        }

        public static String resourceRefreshKey(HotSiteEntity site, UserEntity user) {
            return HotSiteDAOImpl.RedisKey.resourceRefreshKey(site, user);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> findResourceByUser(HotSiteEntity hotSite, UserEntity user) {
        String resourceObjKey = RedisKey.resourceListKey(hotSite, user);
//        String refresh =
//                stringRedisTemplate.opsForValue().get(RedisKey.resourceRefreshKey(hotSite, user));
//        if (Objects.isNull(refresh)) {
//            // refresh未获取到：表示未在10分种内刷新这个站点资源。这时发消息给mq更新即可
//            // 在这里初次访问网页完成初始化
//            return null;
//        }
        BoundListOperations<String, Object> listOps =
                redisTemplate.boundListOps(resourceObjKey);
        List<Object> range = listOps.range(0, 29);
        if (!CollectionUtils.isEmpty(range))
            return range.stream().map(item -> (Map<String, String>) item).collect(Collectors.toList());
        return null;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void saveResource(List<Object> resourceList, HotSiteEntity site, UserEntity user) {
        String resourceObjKey = RedisKey.resourceListKey(site, user);

        Map<String, Map<String, String>> indexMapping = new LinkedHashMap<>();
        resourceList.forEach(item -> {
            Map<String, String> content = (Map<String, String>) item;
            // 当前内容更新时间填充
            content.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            indexMapping.put(content.getOrDefault("title", ""), content);
        });

        BoundListOperations<String, Object> listOps = HotSpringBeanUtils.getRedisTemplate().boundListOps(resourceObjKey);
        // TODO 这里目前是取redis中list所有元素，和当前内容作对比
        List<Object> range = listOps.range(0, -1);
        if (!CollectionUtils.isEmpty(range)) {
            for (Object o : range) {
                Map<String, String> cachedContent = (Map<String, String>) o;
                String title = cachedContent.get("title");
                // 跟当前的获取的content作对比
                if (indexMapping.containsKey(title)) {
                    listOps.remove(1, cachedContent);
                    // 设置最初这个资源的获得时间
                    indexMapping.get(title).put("timeStamp",
                            cachedContent.getOrDefault("timeStamp", String.valueOf(System.currentTimeMillis())));
                }
            }
        }

        // 反转保存
        Object[] objects = reversal(indexMapping.entrySet());
        listOps.leftPushAll(objects);

        // 发送刷新消息
        HotRabbitUtils.createResource(new MessageSiteWrapper(site, UUID.randomUUID().toString()), user);
    }

    private static Object[] reversal(Set<Map.Entry<String, Map<String, String>>> entries) {
        Object[] array = entries.stream().map(Map.Entry::getValue).toArray();
        int L = 0;
        int R = array.length - 1;
        while (L < R) {
            Object temp = array[L];
            array[L++] = array[R];
            array[R--] = temp;
        }
        return array;
    }

}

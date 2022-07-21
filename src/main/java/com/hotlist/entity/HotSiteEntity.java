package com.hotlist.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotlist.common.HotRankListCategory;
import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.core.HotResource;
import com.hotlist.core.filter.SerializeFilterRuler;
import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotRabbitUtils;
import com.hotlist.utils.HotSpringBeanUtils;
import com.hotlist.utils.HotUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@Slf4j
public class HotSiteEntity {

    private String siteName;
    private String alias;
    private String arrayKey;
    private String url;
    private Map<String, String> userAgent;
    private String hotRankList;
    private HotRankListCategory hotRankListCategory;
    private String parseType;
    private Map<String, String> parseContent;
    private SerializeFilterRuler serializeFilterRuler;
    private String resourceFilter;

    private String parseBean;
    private Boolean available;
    private ShowConfig showConfig;

    @Data
    public static class ShowConfig {
        private Map<String, String> config;

        private Map<String, String> pageMappingConfig;

        public String format(String originalString, String targetString, String express) {
            String str = "\\[" + originalString + "\\]";
            // express = https://s.weibo.com/weibo?q={{address}}
            // originalString = 是打发斯蒂芬
            // return https://s.weibo.com/weibo?q=是打发斯蒂芬
            return express.replaceAll(str, targetString);
        }
    }

    public HotSiteEntity() {

    }

    public String getArrayKey() {
        return arrayKey;
    }

    public String getUrl() {
        return url;
    }

    @JSONField(serialize = false)
    @JsonIgnore
    public String getSaveKey() {
        return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName(), alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public HotResource getParserBeanObject() {
        return (HotResource) HotSpringBeanUtils.getBean(parseBean);
    }


    public void saveBySite() {
        String objKey = HotUtil.stringJoin(alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
        HotSpringBeanUtils.getRedisTemplate().opsForHash().put(getSiteHashKeyByCurrentUser(), objKey, this);
    }

    public void delSite() {
        String objKey = HotUtil.stringJoin(alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
        HotSpringBeanUtils.getRedisTemplate().opsForHash().delete(getSiteHashKeyByCurrentUser(), objKey);
    }
    @JSONField(serialize = false)
    @JsonIgnore
    public static String getSiteHashKeyByCurrentUser() {
        return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName());
    }

    @JSONField(serialize = false)
    @JsonIgnore
    public static String getResourceKey() {
        return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName(), "res");
    }

    public static String resourceObjKey(HotSiteEntity hotSite) {
        return HotUtil.stringJoin(getResourceKey(), hotSite.getAlias(), hotSite.getHotRankList(),
                HotUtil.categoryJoin(hotSite.getHotRankListCategory()), "pc");
    }

    @SuppressWarnings("unchecked")
    public void saveByResource(List<Object> parsedResource) {
        String resourceObjKey = resourceObjKey(this);
        Map<String, Map<String, String>> indexMapping = new LinkedHashMap<>();
        parsedResource.forEach(item -> {
            Map<String, String> content = (Map<String, String>) item;
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
                if (indexMapping.containsKey(title)) listOps.remove(1, cachedContent);
                // 设置最初这个资源的获得时间
                indexMapping.get(title).put("timeStamp",
                        cachedContent.getOrDefault("timeStamp", String.valueOf(System.currentTimeMillis())));
            }
        }

        // 反转保存
        Object[] objects = reversal(indexMapping.entrySet());
        listOps.leftPushAll(objects);

        // 发送刷新消息
        String uuid = UUID.randomUUID().toString();
        String refreshKey = HotUtil.stringJoin("refreshKey", HotSiteEntity.resourceObjKey(this));
        HotSpringBeanUtils.stringRedisTemplate.opsForValue().set(refreshKey, uuid, 10, TimeUnit.MINUTES);
        HotRabbitUtils.createResource(new MessageSiteWrapper(this, uuid));
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

    @SuppressWarnings("unchecked")
    @JSONField(serialize = false)
    @JsonIgnore
    public List<Map<String, String>> getResource() {
        String resourceObjKey = resourceObjKey(this);
        String refreshTimestamp = HotSpringBeanUtils.stringRedisTemplate.opsForValue().get("refreshKey:" + resourceObjKey);
        if (Objects.isNull(refreshTimestamp)) return null;

        BoundListOperations<String, Object> listOps = HotSpringBeanUtils.redisTemplate.boundListOps(resourceObjKey);
        List<Object> range = listOps.range(0, 49);
        if (!CollectionUtils.isEmpty(range))
            return range.stream().map(item -> (Map<String, String>) item).collect(Collectors.toList());
        throw new RuntimeException("获取资源失败：初次获取失败");
    }

}

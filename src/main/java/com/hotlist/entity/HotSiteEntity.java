package com.hotlist.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotlist.common.HotRankListCategory;
import com.hotlist.core.HotResource;
import com.hotlist.core.filter.SerializeFilterRuler;
import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotSpringBeanUtils;
import com.hotlist.utils.HotUtil;
import lombok.Data;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
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
    public static String getSiteHashKeyByCurrentUser() {
        return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName());
    }

    @JSONField(serialize = false)
    public static String getResourceKey() {
        return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName(), "res");
    }

    @SuppressWarnings("unchecked")
    public void saveByResource(List<Object> parsedResource) {
        String objKey = HotUtil.stringJoin(getResourceKey(), alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
        Map<String, Object> map = new HashMap<>();
        parsedResource.forEach(item -> {
            Map<String, String> content = (Map<String, String>) item;
            map.put(content.getOrDefault("title", ""), content);
        });

        BoundListOperations<String, Object> listOps = HotSpringBeanUtils.getRedisTemplate().boundListOps(objKey);
        // TODO 这里目前是取redis中list所有元素，和当前内容作对比
        List<Object> range = listOps.range(0, -1);
        if (!CollectionUtils.isEmpty(range)) {
            for (Object o : range) {
                Map<String, String> content = (Map<String, String>) o;
                String title = content.get("title");
                // 跟当前的获取的content作对比
                if (map.containsKey(title)) {
                    listOps.remove(1, content);
                }
            }
        }

        // 反转保存
        Object[] objects = new Object[parsedResource.size()];
        for (int i = 0; i < parsedResource.size(); i++) objects[i] = parsedResource.get(parsedResource.size() - i - 1);
        listOps.leftPushAll(objects);

        // 记录刷新时间
        HotSpringBeanUtils.stringRedisTemplate.opsForValue()
                .set("refresh:" + objKey, String.valueOf(System.currentTimeMillis()));
    }

//    @SuppressWarnings("unchecked")
//    @Deprecated
//    public static Set<ZSetOperations.TypedTuple<Object>> resolveParsedResource(List<Object> parsedResource) {
//        List<ZSetOperations.TypedTuple<Object>> score = parsedResource.stream().map(res -> {
//            Map<String, String> content = (Map<String, String>) res;
//            return new DefaultTypedTuple<Object>(content.get("title") + "_" + content.get("score"), Double.valueOf(content.get("score")));
//        }).collect(Collectors.toList());
//
//        Set<ZSetOperations.TypedTuple<Object>> ans = new HashSet<>(score.size());
//        ans.addAll(score);
//        return ans;
//    }


    @SuppressWarnings("unchecked")
    @JSONField(serialize = false)
    @JsonIgnore
    public List<Map<String, String>> getResource() {
        String objKey = HotUtil.stringJoin(getResourceKey(), alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
        // 当前站点最后更新时间
        String refreshTimestamp = HotSpringBeanUtils.stringRedisTemplate.opsForValue().get("refresh:" + objKey);
        if (Objects.isNull(refreshTimestamp)) return null;

        BoundListOperations<String, Object> listOps = HotSpringBeanUtils.redisTemplate.boundListOps(objKey);
        List<Object> range = listOps.range(0, 49);
        if (!CollectionUtils.isEmpty(range))
            return range.stream().map(item -> (Map<String, String>) item).collect(Collectors.toList());
        return null;
    }

}

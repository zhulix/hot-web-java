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
import org.springframework.data.redis.core.BoundValueOperations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public void saveByResource(List<Object> parsedResource, int timeout, TimeUnit timeUnit) {
        String objKey = HotUtil.stringJoin(getResourceKey(), alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
        BoundValueOperations<String, Object> valueOps = HotSpringBeanUtils.getRedisTemplate().boundValueOps(objKey);
        valueOps.set(parsedResource, timeout, timeUnit);
    }

    @SuppressWarnings("unchecked")
    @JSONField(serialize = false)
    public List<Map<String, String>> getResource() {
        String objKey = HotUtil.stringJoin(getResourceKey(), alias, hotRankList, HotUtil.categoryJoin(hotRankListCategory), "pc");
        return (List<Map<String, String>>) HotSpringBeanUtils.getRedisTemplate().opsForValue().get(objKey);
    }



}

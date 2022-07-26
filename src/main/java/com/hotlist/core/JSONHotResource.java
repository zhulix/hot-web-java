package com.hotlist.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hotlist.core.filter.HotResultWrapper;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JSONHotResource extends HotResourceBase {

    private final ResourceService resourceService;

    public JSONHotResource(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public void save(HotResultWrapper hotResultWrapper, UserEntity user) {
        if (Objects.nonNull(hotResultWrapper.getParsedResource()))
            resourceService.saveResource(hotResultWrapper.getParsedResourceAsList(), hotResultWrapper.getHotSite(), user);
    }

    @Override
    public HotResultWrapper parseResource(HotResourceParser hotResourceParser) {
//        List<Map<String, String>> parseContent = hotResourceParser.getParseContent();
        List<Object> objects = doParseResource(hotResourceParser);

        HotResultWrapper hotResultWrapper = new HotResultWrapper(objects, hotResourceParser.hotSite);
        if (hotResourceParser.context.hasFilter()) {
            hotResourceParser.context.getFilter().doFilter(hotResultWrapper, hotResourceParser.hotSite.getSerializeFilterRuler());
        }

        return hotResultWrapper;
    }

    public List<Object> doParseResource(HotResourceParser hotResourceParser) {
        String arrayKey = hotResourceParser.getArrayKey();
        JSONArray jsonArray = parseContainer(hotResourceParser.resource, arrayKey);
        return jsonArray.stream().map(item -> (JSONObject) item).collect(Collectors.toList());
    }

    private JSONArray parseContainer(String resourceStr, String arrayKey) {
        try {
            return StringUtils.isEmpty(arrayKey) ? JSON.parseArray(resourceStr) : (JSONArray) Ognl.getValue(arrayKey, JSON.parseObject(resourceStr));
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resolve(HotResultWrapper resultWrapper, HotResourceParser hotResourceParser) {
        // 需要解析的内容
        Map<String, String> parseContent = hotResourceParser.getParseContent();
        List<Object> contentByMapping = getContentByMapping(parseContent, resultWrapper);
        resultWrapper.setParsedResource(contentByMapping);
    }

    private List<Object> getContentByMapping(Map<String, String> parseContent, HotResultWrapper hotResultWrapper) {
        HotSiteEntity hotSite = hotResultWrapper.getHotSite();
        HotSiteEntity.ShowConfig showConfig = hotSite.getShowConfig();
        Map<String, String> configMap = hotSite.getShowConfig().getConfig();

        List<Object> jsonObjects = hotResultWrapper.getParsedResourceAsList();
        List<Object> ans = new ArrayList<>(jsonObjects.size());
        jsonObjects.forEach(item -> {
            JSONObject json = (JSONObject) item;
            Map<String, String> content = new HashMap<>();
            parseContent.keySet().forEach(k -> {
                String key = parseContent.get(k);

                String val;
                if (key.contains(".")) val = parseJsonKey(key, json);
                else val = json.getString(parseContent.get(k));
                if (configMap.containsKey(k)) val = showConfig.format(k, val, configMap.get(k));
                content.put(k, val);
            });
            ans.add(content);
        });
        return ans;
    }

    private String parseJsonKey(String resourceStr, JSONObject obj) {
        try {
            return Ognl.getValue(resourceStr, obj).toString();
        } catch (Exception e) {
            log.error("资源：{}, 解析键：{}", resourceStr, obj);
            throw new RuntimeException(e);
        }
    }

}

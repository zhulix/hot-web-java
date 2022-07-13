package com.hotlist.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hotlist.core.filter.HotResultWrapper;
import com.hotlist.entity.HotSiteEntity;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JSONHotResource extends HotResourceBase {

    @Override
    public void save(HotResultWrapper hotResultWrapper) {
        HotSiteEntity hotSite = hotResultWrapper.getHotSite();
        hotSite.saveByResource(hotResultWrapper.getParsedResourceAsList(), 5, TimeUnit.MINUTES);
//        redisTemplate.opsForValue().set(hotSite.getResourceKey(),
//                hotResultWrapper.getParsedResource(),
//                5, TimeUnit.MINUTES);
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
//        List<Object> parsedResource = resultWrapper.getParsedResource();

        Map<String, String> parseContent = hotResourceParser.getParseContent();
//        Map<String, String> map = new HashMap<>(resultWrapper.getParsedResourceAsList().size());
        List<Object> maps = getMaps(parseContent, resultWrapper);
        resultWrapper.setParsedResource(maps);
    }

    private List<Object> getMaps(Map<String, String> parseContent, HotResultWrapper hotResultWrapper) {
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

    private String parseJsonKey(String resourceStr, Object obj) {
        try {
            return (String) Ognl.getValue(obj, JSON.parseObject(resourceStr));
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

}

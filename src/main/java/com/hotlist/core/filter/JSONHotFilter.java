package com.hotlist.core.filter;

import com.alibaba.fastjson2.JSONObject;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JSONHotFilter extends Filter {

    public void doFilter(HotResultWrapper hotResultWrapper, SerializeFilterRuler serializeFilterRuler) {
        List<Object> jsonObjects = hotResultWrapper.getParsedResourceAsList();

        List<Object> collect = jsonObjects.stream()
                .filter(item ->
                        !(mapMatch(serializeFilterRuler.getExcludeMap(), item)
                                || simpleMatch(serializeFilterRuler.hasKey, item, true)
                                || simpleMatch(serializeFilterRuler.notKey, item, false)) )
                .collect(Collectors.toList());
        hotResultWrapper.setParsedResource(collect);
    }

    private boolean mapMatch(Map<String, String> excludeMap, Object item) {
        Set<String> keys = excludeMap.keySet();
        for (String key : keys) {
            if (key.contains("."))
                return getOgnlValue(key, item).equals(excludeMap.get(key));
            else
                return ((JSONObject) item).getString(key).equals(excludeMap.get(key));
        }
        return false;
    }

    private boolean keyMatch(String key, Object jsonObject) {
        if (key.contains("."))
            return Objects.nonNull(getOgnlValue(key, jsonObject));
        else
            return ((JSONObject) jsonObject).containsKey(key);
    }

    private boolean simpleMatch(String key, Object jsonObject, boolean contains) {
        if (StringUtils.isEmpty(key)) return false;
        if (contains) return keyMatch(key, jsonObject);
        else return !keyMatch(key, jsonObject);
    }

//    private boolean custom(JSONObject jsonObject) {
////
//        return false;
//    }

    private Object getOgnlValue(String expression, Object jsonObject) {
        try {
            return Ognl.getValue(expression, jsonObject);
        } catch (OgnlException e) {
            throw new RuntimeException(String.format("filter error [getOgnlValue:%s]", expression));
        }
    }

}

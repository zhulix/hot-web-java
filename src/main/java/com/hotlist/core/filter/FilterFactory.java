package com.hotlist.core.filter;

import java.util.HashMap;
import java.util.Map;

public class FilterFactory {
    public static final Map<String, Filter> FILTER_MAP = new HashMap<>();

    static {
        FILTER_MAP.put("json", new JSONHotFilter());
        FILTER_MAP.put("document", new DocumentFilter());
    }
}

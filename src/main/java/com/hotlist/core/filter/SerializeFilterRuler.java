package com.hotlist.core.filter;

import lombok.Data;

import java.util.Map;

@Data
public class SerializeFilterRuler {

    // 排除包含该键的对象
    String hasKey;

    // 排除不包含改建的对象
    String notKey;

    /**
     * k为键，当前对象val和这个val相等时排除
     */
    Map<String, String> excludeMap;

    Object custom;
}

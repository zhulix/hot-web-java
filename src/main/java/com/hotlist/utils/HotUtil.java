package com.hotlist.utils;

import com.hotlist.common.HotRankListCategory;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class HotUtil {

    public static String stringJoin(String... str) {
        return stringJoin(':', str);
    }

    public static String stringJoin(char symbol, String... str) {
        return StringUtils.join(Arrays.stream(str).collect(Collectors.toList()), symbol);
    }

    public static String categoryJoin(HotRankListCategory hotRankListCategory) {
        if (Objects.isNull(hotRankListCategory.getChild())) return hotRankListCategory.getHotRankListCategory();

        StringBuilder sb = new StringBuilder();
        while (Objects.nonNull(hotRankListCategory)) {
            sb.append(hotRankListCategory.getHotRankListCategory()).append(":");
            hotRankListCategory = hotRankListCategory.getChild();
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }


}

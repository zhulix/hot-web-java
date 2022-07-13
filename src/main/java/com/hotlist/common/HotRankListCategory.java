package com.hotlist.common;

import lombok.Data;

@Data
public class HotRankListCategory {
    /**
     * 排行榜分类：百度实时排行-搜索、文章、小说、电影...
     */
    private String hotRankListCategory = "default";
    /**
     * 子分类
     */
    private HotRankListCategory child;
}

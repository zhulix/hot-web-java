package com.hotlist.common.dto;

import com.hotlist.common.HotRankListCategory;
import lombok.Data;

import java.util.List;

@Data
public class PostTestDto {

    private String siteName;

    // 这个用来做redis库名
    private String rdsName;

    private String url;

    private List<Header> userAgent;

    private String parseType;

    private String keyObj;

//    private String model;

    private List<Format> parseContent;

    private String hotRankList = "default";

    private HotRankListCategory hotRankListCategory = new HotRankListCategory();

    @Data
    public static class Format {
        private String tag;
        private String value;
    }

    @Data
    public static class Header {
        private String k;
        private String v;
    }


}

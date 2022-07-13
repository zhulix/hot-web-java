package com.hotlist.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hotlist.common.HotRankListCategory;
import com.hotlist.core.filter.SerializeFilterRuler;
import com.hotlist.entity.HotSiteEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HotSiteDto {
    private String siteName;

    @JsonAlias("rdsName")
    private String alias;

    @JsonAlias("keyObj")
    private String arrayKey;

    private String url;

    private String hotRankList = "default";

    private Boolean available;

//    private Map<String, String> userAgent;

    @JsonAlias("category")
    private HotRankListCategory hotRankListCategory;

    private String parseBean;

    private String parseType;

    private Map<String, String> parseContent;

    @JsonAlias("excludeKey")
    private SerializeFilterRuler serializeFilterRuler;

    private HotSiteEntity.ShowConfig showConfig;

}

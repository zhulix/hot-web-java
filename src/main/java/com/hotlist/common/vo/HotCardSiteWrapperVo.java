package com.hotlist.common.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HotCardSiteWrapperVo {

    private String siteName;

    private String siteUrl;

    private Integer sort;

    private List<HotResource> hotResource;

    private List<Map<String, String>> resultSite;

    private String hotRankListName;

    private Map<String, String> pageMappingConfig;

    @Data
    static class HotResource {
        // 12h 24h 48h榜单名字
        private String hotResourceName = "default";

        // 默认榜单？
        private boolean isDefault;
    }

}

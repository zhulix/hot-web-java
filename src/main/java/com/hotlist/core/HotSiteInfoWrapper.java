package com.hotlist.core;

import com.hotlist.common.HotRankListCategory;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.utils.HotUtil;

import java.util.Map;

public class HotSiteInfoWrapper {

    enum Model {
        PC("PC", "pc"),
        ANDROID("ANDROID", "android"),
        H5("H5", "h5"),
        CUSTOMER("CUSTOMER", "customer");

        Model(String name, String value) {
            this.name = name;
            this.value = value;
        }
        private String name;

        private String value;

        public String getValue() {
            return value;
        }
    }
    Model model = Model.PC;

    HotSiteEntity hotSite;

    public HotSiteInfoWrapper(HotSiteEntity hotSite) {
        this.hotSite = hotSite;
    }

    /**
     * 站点： 百度、值得买、微博...
     */
    String name;

    /**
     * 排行榜: 百度实时排行、
     */
    String hotRankList = "default";

    HotRankListCategory hotRankListCategory;


    Map<String, String> header;

    /**
     * 解析内容
     */
    Map<String, String> parseContent;

    public String getUrlKey() {
        return HotUtil.stringJoin(getFormatKey(), model.getValue(), "url");
    }

    public String getFormatKey() {
        return HotUtil.stringJoin("hot", name, hotRankList, HotUtil.categoryJoin(hotRankListCategory));
    }

}


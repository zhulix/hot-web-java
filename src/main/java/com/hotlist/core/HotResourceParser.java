package com.hotlist.core;

import com.hotlist.core.filter.Filter;
import com.hotlist.entity.HotSiteEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HotResourceParser {

    Context context = new Context();

    public HotResourceParser(String resource, HotSiteEntity hotSite) {
        this.resource = resource;
        this.hotSite = hotSite;
    }

    public static class Context {

        /**
         * 一个站点应该只有一个过滤规则，如果过滤东西复杂，应该使用自定义过滤器
         */
        private Filter filter;

        public void configFilter(Filter filter) {
            this.filter = filter;
        }

        public Filter getFilter() {
            return this.filter;
        }

        public boolean hasFilter() {
            return Objects.nonNull(filter);
        }


    }

    String resource;

    HotSiteEntity hotSite;

    Object result;

    public String getArrayKey() {
        return hotSite.getArrayKey();
    }

    public String getUrl() {
        return hotSite.getUrl();
    }

    public Map<String, String> getParseContent() {
        return hotSite.getParseContent();
    }

}

package com.hotlist.core.filter;

import com.hotlist.entity.HotSiteEntity;

import java.util.List;


public class HotResultWrapper {

    /**
     */
    @SuppressWarnings("unchecked")
    public List<Object> getParsedResourceAsList() {
        return (List<Object>) parsedResource;
    }

    public void setParsedResource(List<Object> parsedResource) {
        this.parsedResource = parsedResource;
    }

    public Object getParsedResource() {
        return parsedResource;
    }

    Object parsedResource;

    HotSiteEntity hotSite;

    public HotResultWrapper(Object parsedResource, HotSiteEntity hotSite) {
        this.parsedResource = parsedResource;
        this.hotSite = hotSite;
    }

    public HotResultWrapper(Object parsedResource) {
        this.parsedResource = parsedResource;
    }

    public void setHotSite(HotSiteEntity hotSite) {
        this.hotSite = hotSite;
    }

    public HotSiteEntity getHotSite() {
        return hotSite;
    }
}

package com.hotlist.core;

import com.hotlist.core.filter.HotResultWrapper;

import java.util.List;

public interface HotResource {

    List<Object> fetch(HotSiteInfoWrapper hotSiteInfoWrapper);

    default void save(HotResultWrapper hotResultWrapper) {
        throw new RuntimeException("[save] method not implement");
    }

}

package com.hotlist.core;

import com.hotlist.core.filter.HotResultWrapper;
import com.hotlist.entity.UserEntity;

public interface HotResource {

    HotResultWrapper fetch(HotSiteInfoWrapper hotSiteInfoWrapper);

    default void save(HotResultWrapper hotResultWrapper, UserEntity user) {
        throw new RuntimeException("[save] method not implement");
    }

}

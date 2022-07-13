package com.hotlist.service;

import com.hotlist.common.dto.HotSiteDto;
import com.hotlist.common.dto.PostWrapper;
import com.hotlist.entity.HotSiteEntity;

public interface HotRdbService {

    @Deprecated
    void saveByKey(String key, String value);

    @Deprecated
    void savePost(PostWrapper postWrapper);

    void savePost(HotSiteDto hotSiteDto);

    HotSiteEntity getConfigByKey(String key);

    Object getResourceByKey(String key);
}

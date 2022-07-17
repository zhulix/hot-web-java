package com.hotlist.common.to;

import com.hotlist.entity.HotSiteEntity;
import lombok.Data;

@Data
public class MessageSiteWrapper {
    private HotSiteEntity site;
    private String uuid;

    public MessageSiteWrapper(HotSiteEntity site, String uuid) {
        this.site = site;
        this.uuid = uuid;
    }

    public MessageSiteWrapper() {
    }
}

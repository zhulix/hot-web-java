package com.hotlist.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@Accessors(chain = true)
public class HotResourceESModel {

    private String uid;

    public String getUid() {
        return DigestUtils.md5Hex(this.toString());
    }

    private String title;

    private String address;

    private String timeStamp;

    private String siteName;

    private String hotRankList;

    private String source;
    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", siteName='" + siteName + '\'' +
                '}';
    }
//    private HotRankListCategory hotRankListCategory;

}

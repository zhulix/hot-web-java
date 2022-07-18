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

    @Override
    public String toString() {
        return "HotResourceESModel{" +
                "title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", timeStamp=" + timeStamp +
                ", siteName='" + siteName + '\'' +
                '}';
    }
//    private HotRankListCategory hotRankListCategory;

}

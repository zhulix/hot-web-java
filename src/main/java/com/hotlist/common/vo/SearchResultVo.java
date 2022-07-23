package com.hotlist.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SearchResultVo {
    private String title;

    private String address;

    private String timeStamp;

    private String siteName;

    private String hotRankList;

//    private Object resource;
}

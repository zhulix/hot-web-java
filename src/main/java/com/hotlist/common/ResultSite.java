package com.hotlist.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResultSite {

    private String hotTitle;

    private String hotVal;

    private String hotUrl;

    private Object resource;
}

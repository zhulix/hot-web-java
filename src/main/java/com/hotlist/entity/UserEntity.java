package com.hotlist.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.hotlist.utils.HotUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserEntity {

    private String userName;

    private String password;

    private String showSchema;

    @JSONField(serialize = false)
    public String getKey() {
        return HotUtil.stringJoin("hot", "user");
    }


}

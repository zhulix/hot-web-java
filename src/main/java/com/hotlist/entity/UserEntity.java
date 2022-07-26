package com.hotlist.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserEntity {

    private String userName;

    private String password;

    private String showSchema;

}

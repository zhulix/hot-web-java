package com.hotlist.service;

import com.hotlist.entity.UserEntity;

public interface UserService {

    Boolean saveUser(UserEntity user);

    UserEntity selectByUserName(String userName);

    void update(UserEntity setShowSchema);
}

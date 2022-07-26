package com.hotlist.dao;

import com.hotlist.entity.UserEntity;


public interface UserDAO {


    Boolean saveUser(UserEntity user);

    UserEntity findByUserName(String userName);

    void updateUser(UserEntity user);
}

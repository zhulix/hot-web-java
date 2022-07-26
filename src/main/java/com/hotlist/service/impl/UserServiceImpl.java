package com.hotlist.service.impl;

import com.hotlist.dao.UserDAO;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Boolean saveUser(UserEntity user) {
        return userDAO.saveUser(user);
    }

    @Override
    public UserEntity selectByUserName(String userName) {
        return userDAO.findByUserName(userName);
    }

    @Override
    public void update(UserEntity user) {
        userDAO.updateUser(user);
    }
}

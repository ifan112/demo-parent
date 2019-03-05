package com.ifan112.demo.mybatis.spring.service.impl;

import com.ifan112.demo.mybatis.spring.entity.User;
import com.ifan112.demo.mybatis.spring.mapper.UserMapper;
import com.ifan112.demo.mybatis.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean save(User user) {
        return userMapper.insert(user) == 1;
    }

    @Override
    public User get(long id) {
        return userMapper.selectOne(id);
    }

    @Override
    public List<User> allUsers() {
        return userMapper.selectAll();
    }

    @Override
    public boolean delete(long id) {
        return userMapper.delete(id) == 1;
    }
}

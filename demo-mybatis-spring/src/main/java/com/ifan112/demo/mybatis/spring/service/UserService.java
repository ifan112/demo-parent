package com.ifan112.demo.mybatis.spring.service;

import com.ifan112.demo.mybatis.spring.entity.User;

import java.util.List;

public interface UserService {

    boolean save(User user);

    User get(long id);

    List<User> allUsers();

    boolean delete(long id);
}

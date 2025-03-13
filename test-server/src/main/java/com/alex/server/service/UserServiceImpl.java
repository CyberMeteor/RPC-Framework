package com.alex.server.service;

import cn.hutool.core.util.IdUtil;
import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.annotation.Limit;

public class UserServiceImpl implements UserService {
    @Limit(permitsPerSecond = 5, timeout = 0)
    @Override
    public User getUser(Long id) {
        return User.builder()
                .id(++id)
                .name("Alice")
                .build();
    }
}

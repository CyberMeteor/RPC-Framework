package com.alex.server;

import cn.hutool.core.util.IdUtil;
import com.alex.api.User;
import com.alex.api.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        return User.builder()
                .id(id)
                .name(IdUtil.fastSimpleUUID())
                .build();
    }
}

package com.alex.api;

import com.alex.rpc.annotation.Retry;

public interface UserService {
    @Retry
    User getUser(Long id);
}
package com.alex.api;

import com.alex.rpc.annotation.Breaker;
import com.alex.rpc.annotation.Retry;

public interface UserService {
//    @Retry(maxAttempts = 4,  delay = 5000)
    @Breaker(windowTime = 30000)
    User getUser(Long id);
}
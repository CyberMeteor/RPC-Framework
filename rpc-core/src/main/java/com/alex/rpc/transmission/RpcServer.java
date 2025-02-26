package com.alex.rpc.transmission;

import com.alex.rpc.config.RpcServiceConfig;

public interface RpcServer {
    void start();

    void  publishService(RpcServiceConfig config);
}

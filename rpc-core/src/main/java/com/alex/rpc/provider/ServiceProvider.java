package com.alex.rpc.provider;

import com.alex.rpc.config.RpcServiceConfig;

public interface ServiceProvider {
    void publishService(RpcServiceConfig config);

    Object getService(String rpcServiceName);
}

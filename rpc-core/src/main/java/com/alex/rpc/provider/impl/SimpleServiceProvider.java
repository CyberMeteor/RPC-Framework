package com.alex.rpc.provider.impl;

import cn.hutool.core.collection.CollUtil;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.provider.ServiceProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    @Override
    public void publishService(RpcServiceConfig config) {
        List<String> rpcServiceNames = config.rpcServiceNames();

        if (CollUtil.isEmpty(rpcServiceNames)) {
            throw new RuntimeException("This service does not implement an interface");
        }

        rpcServiceNames.forEach(rpcServiceName -> SERVICE_CACHE.put(rpcServiceName, config.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new IllegalArgumentException("Can't find corresponding service: " + rpcServiceName);
        }
        return SERVICE_CACHE.get(rpcServiceName);
    }
}

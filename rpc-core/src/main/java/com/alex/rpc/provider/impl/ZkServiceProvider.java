package com.alex.rpc.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.provider.ServiceProvider;
import com.alex.rpc.registry.ServiceRegistry;
import com.alex.rpc.registry.impl.ZkServiceRegistry;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ZkServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    public ZkServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames()
                .forEach(rpcServiceName -> publishService(rpcServiceName, config.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (StrUtil.isBlank(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName is null or empty");
        }

        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName not exist");
        }

        return SERVICE_CACHE.get(rpcServiceName);
    }

    @SneakyThrows
    private void publishService(String rpcServiceName, Object service) {
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;

        InetSocketAddress address = new InetSocketAddress(host, port);
        serviceRegistry.registerService(rpcServiceName, address);

        SERVICE_CACHE.put(rpcServiceName, service);
    }
}

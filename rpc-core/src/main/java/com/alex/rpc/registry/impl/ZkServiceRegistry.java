package com.alex.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.registry.ServiceRegistry;
import com.alex.rpc.registry.zk.ZkClient;
import com.alex.rpc.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    private final ZkClient zkClient;

    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        log.info("Service register rpc service name:{}, address:{}", rpcServiceName, address);

        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcServiceName
                + StrUtil.SLASH
                + IPUtils.toIpPort(address);

        zkClient.createPersistentNode(path);
    }
}

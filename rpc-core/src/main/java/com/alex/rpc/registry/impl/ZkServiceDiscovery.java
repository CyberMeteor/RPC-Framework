package com.alex.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.loadbalance.LoadBalance;
import com.alex.rpc.loadbalance.impl.RandomLoadBalance;
import com.alex.rpc.registry.ServiceDiscovery;
import com.alex.rpc.registry.zk.ZkClient;
import com.alex.rpc.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this(
                SingletonFactory.getInstance(ZkClient.class),
                SingletonFactory.getInstance(RandomLoadBalance.class)
        );
    }

    public ZkServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcReq rpcReq) {
        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcReq.rpcServiceName();

        List<String> children = zkClient.getChildrenNode(path);
        String address = loadBalance.select(children);

        return IPUtils.toInetSocketAddress(address);
    }
}

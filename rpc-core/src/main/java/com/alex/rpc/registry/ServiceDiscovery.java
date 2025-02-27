package com.alex.rpc.registry;

import com.alex.rpc.dto.RpcReq;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcReq rpcReq);
}

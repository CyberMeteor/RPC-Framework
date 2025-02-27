package com.alex.client.utils;

import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.socket.client.SocketRpcClient;

public class ProxyUtils {
    private static final RpcClient rpcClient = new SocketRpcClient("127.0.0.1", 8888);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}

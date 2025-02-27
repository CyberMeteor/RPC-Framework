package com.alex.client;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.socket.client.SocketRpcClient;
import com.alex.rpc.util.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        UserService userService = getProxy(UserService.class);
        User user = userService.getUser(1L);
        System.out.println(user);

    }

    private static <T> T getProxy(Class<T> clazz) {
        RpcClient rpcClient = new SocketRpcClient("127.0.0.1", 8888);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        return rpcClientProxy.getProxy(clazz);
    }
}

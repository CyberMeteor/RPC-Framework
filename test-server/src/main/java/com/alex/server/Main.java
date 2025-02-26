package com.alex.server;

import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;
import com.alex.server.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());

        RpcServer rpcServer = new SocketRpcServer(8888);
        rpcServer.publishService(config);

        rpcServer.start();

//        UserServiceImpl userService = new UserServiceImpl();
//
//        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig("1.0.0", "common", userService);
//        System.out.println(rpcServiceConfig.rpcServiceNames());
    }
}

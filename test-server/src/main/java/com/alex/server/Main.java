package com.alex.server;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;
import com.alex.server.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());

        RpcServer rpcServer = new SocketRpcServer(8888);
        rpcServer.publishService(config);

        rpcServer.start();

    }
}

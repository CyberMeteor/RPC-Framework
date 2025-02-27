package com.alex.server;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;
import com.alex.server.service.UserServiceImpl;
import org.apache.log4j.BasicConfigurator;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());

        RpcServer rpcServer = new SocketRpcServer();
        rpcServer.publishService(config);

        rpcServer.start();

    }
}

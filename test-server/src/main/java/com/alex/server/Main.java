package com.alex.server;

import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;

public class Main {
    public static void main(String[] args) {
        RpcServer rpcServer = new SocketRpcServer(8888);

        rpcServer.start();
    }
}

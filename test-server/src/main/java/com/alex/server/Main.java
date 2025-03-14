package com.alex.server;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.serialize.Serializer;
import com.alex.rpc.serialize.impl.HessianSerializer;
import com.alex.rpc.serialize.impl.ProtostuffSerializer;
import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.netty.server.NettyRpcServer;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;
import com.alex.server.service.UserServiceImpl;
import org.apache.log4j.BasicConfigurator;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
//        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());
//
//        RpcServer rpcServer = new NettyRpcServer();
//        rpcServer.publishService(config);
//
//        rpcServer.start();

//        Serializer serializer = SingletonFactory.getInstance(HessianSerializer.class);
        Serializer serializer = SingletonFactory.getInstance(ProtostuffSerializer.class);

        RpcReq rpcReq = RpcReq.builder()
                .reqId("123321")
                .interfaceName("qwert")
                .paramTypes(new Class<?>[]{String.class, Long.class})
                .build();

        byte[] data = serializer.serialize(rpcReq);
        RpcReq deserializedReq = serializer.deserialize(data, RpcReq.class);

        System.out.println(deserializedReq);
    }
}

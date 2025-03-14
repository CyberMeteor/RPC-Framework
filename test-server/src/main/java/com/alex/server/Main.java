package com.alex.server;

import cn.hutool.core.collection.ListUtil;
import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.loadbalance.LoadBalance;
import com.alex.rpc.loadbalance.impl.RoundLoadBalance;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.serialize.Serializer;
import com.alex.rpc.serialize.impl.HessianSerializer;
import com.alex.rpc.serialize.impl.ProtostuffSerializer;
import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.netty.server.NettyRpcServer;
import com.alex.rpc.transmission.socket.server.SocketRpcServer;
import com.alex.server.service.UserServiceImpl;
import org.apache.log4j.BasicConfigurator;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
//        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());
//
//        RpcServer rpcServer = new NettyRpcServer();
//        rpcServer.publishService(config);
//
//        rpcServer.start();

        LoadBalance loadBalance = SingletonFactory.getInstance(RoundLoadBalance.class);

        List<String> list = ListUtil.of("ip1:port1", "ip2:port2", "ip3:port3", "ip4:port4");

        for (int i = 0; i < 10; i++) {
            String select = loadBalance.select(list);
            System.out.println(select);
        }
    }
}

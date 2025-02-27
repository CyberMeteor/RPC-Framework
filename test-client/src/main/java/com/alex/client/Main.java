package com.alex.client;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.socket.client.SocketRpcClient;
import com.alex.rpc.util.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
//        UserService userService = new UserServiceImpl();
//        User user = userService.getUser(1L);
//        System.out.println(user);

        RpcClient rpcClient = new SocketRpcClient("127.0.0.1", 8888);

        RpcReq req = RpcReq.builder()
                .reqId("1213")
                .interfaceName("com.alex.api.UserService")
                .methodName("getUser")
                .params(new Object[]{1L})
                .paramTypes(new Class[]{Long.class})
                .build();


//        ExecutorService threadPool = ThreadPoolUtils.createIoIntensiveThreadPool("test");
//        for (int i = 0; i < 10; i++) {
//            threadPool.submit(() -> {
//                RpcResp<?> rpcResp = rpcClient.sendReq(req);
//                System.out.println(rpcResp.getData());
//            });
//        }

        RpcResp<?> rpcResp = rpcClient.sendReq(req);
        System.out.println(rpcResp.getData());
    }
}

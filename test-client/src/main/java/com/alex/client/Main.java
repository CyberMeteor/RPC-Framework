package com.alex.client;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.transmission.RpcClient;

public class Main {
    public static void main(String[] args) {
//        UserService userService = new UserServiceImpl();
//        User user = userService.getUser(1L);
//        System.out.println(user);

//        RpcClient rpcClient = new RpcClient() {
//            @Override
//            public RpcResp<?> sendReq(RpcReq req) {
//                return null;
//            }
//        };




    }

//    private static <T> T invoke(Long id) {
//        RpcClient rpcClient;
//
//        RpcReq req = RpcReq.builder()
//                .reqId("1213")
//                .interfaceName("com.alex.api.UserService")
//                .methodName("getUser")
//                .params(new Object[]{1L})
//                .paramTypes(new Class[]{Long.class})
//                .build();
//
//        RpcResp<?> rpcResp = rpcClient.sendReq(req);
//        return (T) rpcResp.getData();
//    }
}

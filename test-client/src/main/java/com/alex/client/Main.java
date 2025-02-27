package com.alex.client;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.client.utils.ProxyUtils;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.socket.client.SocketRpcClient;
import com.alex.rpc.util.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        UserService userService = ProxyUtils.getProxy(UserService.class);
        User user = userService.getUser(1L);
        System.out.println(user);
    }
}

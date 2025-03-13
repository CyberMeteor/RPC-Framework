package com.alex.client;

import com.alex.api.User;
import com.alex.api.UserService;
import com.alex.client.utils.ProxyUtils;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.proxy.RpcClientProxy;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.netty.client.NettyRpcClient;
import com.alex.rpc.transmission.socket.client.SocketRpcClient;
import com.alex.rpc.util.ThreadPoolUtils;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        UserService userService = ProxyUtils.getProxy(UserService.class);
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        while (true) {
            System.out.println("Please input request times: ");
            int n = scanner.nextInt();
            System.out.println("Please input id: ");
            long id = scanner.nextLong();

            for (int i = 0; i < n; i++) {
                executorService.execute(() -> {
                    try {
                        User user1 = userService.getUser(id);
                        System.out.println(user1);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }
    }
}

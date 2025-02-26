package com.alex.rpc.transmission.socket.server;

import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.handler.RpcReqHandler;
import com.alex.rpc.provider.ServiceProvider;
import com.alex.rpc.provider.impl.SimpleServiceProvider;
import com.alex.rpc.transmission.RpcServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final RpcReqHandler rpcReqHandler;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(int port) {
        this(port, new SimpleServiceProvider());
    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server started on port {}", port);

            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                RpcReq rpcReq = (RpcReq) inputStream.readObject();
                System.out.println(rpcReq);

                // Pretending to call the method implemented by the interface in rpcReq
                Object data = rpcReqHandler.invoke(rpcReq);

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                RpcResp<?> rpcResp = RpcResp.success(rpcReq.getReqId(), data);
                outputStream.writeObject(rpcResp);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("Server error!", e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }

}

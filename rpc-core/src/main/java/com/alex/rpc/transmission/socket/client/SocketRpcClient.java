package com.alex.rpc.transmission.socket.client;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.provider.impl.ZkServiceProvider;
import com.alex.rpc.registry.ServiceDiscovery;
import com.alex.rpc.registry.impl.ZkServiceDiscovery;
import com.alex.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public SocketRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public RpcResp<?> sendReq(RpcReq rpcReq) {
        InetSocketAddress address = serviceDiscovery.lookupService(rpcReq);

        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(rpcReq);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object o = inputStream.readObject();

            return (RpcResp<?>) o;
        } catch (Exception e) {
            log.error("Fail to send rpc request", e);
        }
        return null;
    }
}

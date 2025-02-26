package com.alex.rpc.transmission.socket.client;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcClient {
    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        try (Socket socket = new Socket("127.0.0.1", 8888)) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(req);
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

package com.alex.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.RpcRespStatus;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.socket.client.SocketRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final RpcServiceConfig config;

    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient, new RpcServiceConfig());
    }

    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcReq rpcReq = RpcReq.builder()
                .reqId(IdUtil.fastSimpleUUID())
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .version(config.getVersion())
                .group(config.getGroup())
                .build();

        RpcResp<?> rpcResp = rpcClient.sendReq(rpcReq);

        check(rpcReq, rpcResp);

        return rpcResp.getData();
    }

    private void check(RpcReq rpcReq, RpcResp<?> rpcResp) {
        if (Objects.isNull(rpcResp)) {
            throw new RuntimeException("rpcResp is null");
        }

        if (!Objects.equals(rpcReq.getReqId(), rpcResp.getReqId())) {
            throw new RuntimeException("Request id is different from response id");
        }

        if (RpcRespStatus.isFailed(rpcResp.getCode())) {
            throw new RuntimeException("RpcRespStatus is failed: " + rpcResp.getMsg());
        }
    }
}

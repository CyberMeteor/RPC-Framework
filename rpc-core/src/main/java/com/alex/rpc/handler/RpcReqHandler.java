package com.alex.rpc.handler;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcReqHandler {
    private final ServiceProvider  serviceProvider;

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows
    public Object invoke(RpcReq rpcReq) {
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        log.debug("invoke corresponding rpc service: {}", service.getClass().getCanonicalName());
        Method method = service.getClass().getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());
        return method.invoke(service, rpcReq.getParams());
    }
}

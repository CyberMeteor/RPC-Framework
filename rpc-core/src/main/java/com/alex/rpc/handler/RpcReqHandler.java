package com.alex.rpc.handler;

import com.alex.rpc.annotation.Limit;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.exception.RpcException;
import com.alex.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcReqHandler {
    private final ServiceProvider  serviceProvider;
    private static final Map<String, RateLimiter> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows
    public Object invoke(RpcReq rpcReq) {
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        log.debug("invoke corresponding rpc service: {}", service.getClass().getCanonicalName());
        Method method = service.getClass().getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());

        Limit limit = method.getAnnotation(Limit.class);
        if (Objects.isNull(limit)) {
            return method.invoke(service, rpcReq.getParams());
        }

        RateLimiter rateLimiter = RATE_LIMITER_MAP.computeIfAbsent(rpcServiceName, __ ->
                RateLimiter.create(limit.permitsPerSecond())
        );

        if (rateLimiter.tryAcquire(limit.timeout(), TimeUnit.MILLISECONDS)) {
            throw new RpcException("System is busy, please try again later");
        }

        return method.invoke(service, rpcReq.getParams());
    }
}

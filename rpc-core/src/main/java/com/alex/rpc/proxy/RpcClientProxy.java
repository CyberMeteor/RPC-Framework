package com.alex.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.alex.rpc.annotation.Breaker;
import com.alex.rpc.annotation.Retry;
import com.alex.rpc.breaker.CircuitBreaker;
import com.alex.rpc.breaker.CircuitBreakerManager;
import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.RpcRespStatus;
import com.alex.rpc.exception.RpcException;
import com.alex.rpc.transmission.RpcClient;
import com.github.rholder.retry.*;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

        Breaker breaker = method.getAnnotation(Breaker.class);
        if (Objects.isNull(breaker)) {
            return sendReqWithRetry(rpcReq, method);
        }

        CircuitBreaker circuitBreaker = CircuitBreakerManager.get(rpcReq.rpcServiceName(), breaker);
        if (!circuitBreaker.canReq()) {
            throw new RpcException("It has been broken by the circuit breaker");
        }

        try {
            Object o = sendReqWithRetry(rpcReq, method);
            circuitBreaker.success();
            return o;
        } catch (Exception e) {
            circuitBreaker.fail();
            throw e;
        }
    }

    @SneakyThrows
    private Object sendReqWithRetry(RpcReq rpcReq, Method method) {
        Retry retry = method.getAnnotation(Retry.class);
        if (Objects.isNull(retry)) {
            return sendReq(rpcReq);
        }

        Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfExceptionOfType(retry.value())
                .withStopStrategy(StopStrategies.stopAfterAttempt(retry.maxAttempts()))
                .withWaitStrategy(WaitStrategies.fixedWait(retry.delay(), TimeUnit.MILLISECONDS))
                .build();

        return retryer.call(() -> sendReq(rpcReq));
    }

    @SneakyThrows
    private Object sendReq(RpcReq rpcReq) {
        Future<RpcResp<?>> future = rpcClient.sendReq(rpcReq);
        RpcResp<?> rpcResp = future.get();
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

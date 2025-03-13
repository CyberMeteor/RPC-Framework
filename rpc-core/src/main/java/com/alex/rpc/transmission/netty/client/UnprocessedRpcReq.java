package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.exception.RpcException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRpcReq {
    private static final Map<String, CompletableFuture<RpcResp<?>>> RESP_CF_MAP = new ConcurrentHashMap<>();

    public static void put(String reqId, CompletableFuture<RpcResp<?>> cf) {
        RESP_CF_MAP.put(reqId, cf);
    }

    public static void complete(RpcResp<?> resp) {
        CompletableFuture<RpcResp<?>> cf = RESP_CF_MAP.remove(resp.getReqId());

        if (Objects.isNull(cf)) {
            throw new RpcException("Unprocessed rpc request error");
        }

        cf.complete(resp);
    }
}

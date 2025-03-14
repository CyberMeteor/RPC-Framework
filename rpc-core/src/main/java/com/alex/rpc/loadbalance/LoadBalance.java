package com.alex.rpc.loadbalance;

import com.alex.rpc.dto.RpcReq;

import java.util.List;

public interface LoadBalance {
    String select(List<String> list, RpcReq rpcReq);
}

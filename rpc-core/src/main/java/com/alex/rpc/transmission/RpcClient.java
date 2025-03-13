package com.alex.rpc.transmission;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;

import java.util.concurrent.Future;

public interface RpcClient {
    Future<RpcResp<?>> sendReq(RpcReq req);
}

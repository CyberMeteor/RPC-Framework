package com.alex.rpc.transmission;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;

public interface RpcClient {
    RpcResp<?> sendReq(RpcReq req);
}

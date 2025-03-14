package com.alex.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.loadbalance.LoadBalance;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        return RandomUtil.randomEle(list);
    }
}

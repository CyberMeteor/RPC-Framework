package com.alex.rpc.loadbalance;

import java.util.List;

public interface LoadBalance {
    public String select(List<String> list);
}

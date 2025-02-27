package com.alex.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.alex.rpc.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

@Slf4j
public class ZkClient {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private CuratorFramework client;

    public ZkClient() {
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    public  ZkClient(String hostname, int port) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        this.client = CuratorFrameworkFactory.builder()
                // server list
                .connectString(hostname + StrUtil.COLON + port)
                .retryPolicy(retryPolicy)
                .build();

        log.info("Begin to connect to zookeeper....");
        this.client.start();
        log.info("zookeeper connect success");
    }
}

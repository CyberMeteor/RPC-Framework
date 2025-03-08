package com.alex.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkClient {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private final CuratorFramework client;

    //  /RPC-Framework/rpcServiceName/ip:port
    private static final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();

    // Key: /RPC-Framework/rpcServiceName  Value:childrenNode [ip:port]
    private static final Set<String> SERVICE_ADDRESS_SET = ConcurrentHashMap.newKeySet();

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

    @SneakyThrows
    public void createPersistentNode(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path is blank");
        }

        if (SERVICE_ADDRESS_SET.contains(path)) {
            log.info("Zookeeper node already exists: {}", path);
            return;
        }

        if (client.checkExists().forPath(path) != null) {
            SERVICE_ADDRESS_SET.add(path);
            log.info("Zookeeper node already exists: {}", path);
            return;
        }

        log.info("Creating Zookeeper node: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }

    @SneakyThrows
    public List<String> getChildrenNode(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path is blank");
        }

        if (SERVICE_ADDRESS_CACHE.containsKey(path)) {
            return SERVICE_ADDRESS_CACHE.get(path);
        }

        List<String> children = client.getChildren().forPath(path);
        SERVICE_ADDRESS_CACHE.put(path, children);

        watchNode(path);

        return children;
    }

    public void clearAll(InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address is null");
        }

        SERVICE_ADDRESS_SET.forEach(path -> {
            if (path.endsWith(IPUtils.toIpPort(address))) {
                log.info("Clear Zookeeper node {}", path);
                try {
                    client.delete().deletingChildrenIfNeeded().forPath(path);
                } catch (Exception e) {
                    log.error("Failed to delete Zookeeper node: {} ", path, e);
                }
            }
        });
    }

    @SneakyThrows
    private void watchNode(String path) {
        if (StrUtil.isBlank(path)) {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);

            // Register a child node listener for a certain node
            PathChildrenCacheListener pathChildrenCacheListener = (curClient, event) -> {
                List<String> children = curClient.getChildren().forPath(path);
                SERVICE_ADDRESS_CACHE.put(path, children);
            };

            pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
            pathChildrenCache.start();
        }
    }
}

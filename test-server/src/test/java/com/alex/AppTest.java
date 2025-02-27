package com.alex;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @SneakyThrows
    public static void main(String[] args) {

        // Reset strategy
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // Server List
                .connectString("localhost:2181")
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();

        // When the parent node does not exist, it will automatically create a parent node. It is more recommended to use it
//        zkClient.create()
//                .creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .forPath("/node1/00002","test1".getBytes());
//
//        Stat stat = zkClient.checkExists().forPath("/node1/00002");
//        System.out.println(stat != null);
//
//        byte[] bytes = zkClient.getData().forPath("/node1/00002");
//        System.out.println(new String(bytes));
//
//        zkClient.setData().forPath("/node1/00002","test2".getBytes());
//
//        bytes = zkClient.getData().forPath("/node1/00002");
//        System.out.println(new String(bytes));
//
//        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");

//        String path = "/n1";
//
//        NodeCache nodeCache = new NodeCache(zkClient, path);
//
//        // register
//        NodeCacheListener listener = () -> {
//            if (nodeCache.getCurrentData() != null) {
//                String data = new String(nodeCache.getCurrentData().getData());
//                System.out.println("node data changed: " + data);
//            } else {
//                System.out.println("node deleted");
//            }
//        };
//        nodeCache.getListenable().addListener(listener);
//
//        nodeCache.start();
//
//        System.in.read();

        String path = "/node1";

        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);

        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                System.out.println("child node removed");
            } else if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                System.out.println("child node added");
            } else if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                System.out.println("child node updated");
            }
        };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();

        System.in.read();
    }
}

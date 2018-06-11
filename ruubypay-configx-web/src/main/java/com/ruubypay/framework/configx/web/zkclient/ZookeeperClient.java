package com.ruubypay.framework.configx.web.zkclient;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * zookeeper客户端
 * @author chenhaiyang
 */
public class ZookeeperClient {
    /**
     * zk地址
     */
    private String zkAddress;
    /**
     * zk客户端
     */
    private CuratorFramework client;

    public ZookeeperClient(String zkAddress){
        this.zkAddress=zkAddress;
    }

    @PostConstruct
    private void init() {
        client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        client.start();
    }

    @PreDestroy
    private void destroy() {
        if (client != null) {
            client.close();
        }
    }

    public CuratorFramework getClient() {
        return client;
    }
}

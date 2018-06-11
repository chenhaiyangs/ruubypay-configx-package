package com.ruubypay.framework.configx.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * sessionConnection连接超时监听器
 * @author chenhaiyang
 */
public class SessionConnectionListener implements ConnectionStateListener {

    /**
     * 配置组对象
     */
    private ZookeeperConfigGroup zookeeperConfigGroup;
    /**
     * nodeListener
     */
    private CuratorListener nodeListener;

    SessionConnectionListener(ZookeeperConfigGroup zookeeperConfigGroup, CuratorListener listener){
        this.zookeeperConfigGroup = zookeeperConfigGroup;
        this.nodeListener=listener;
    }

    /**
     * 网络重连上时需要重新load数据，
     * 重新注册节点监听，并清除失效的监听
     * @param client zk客户端
     * @param connectionState 连接状态
     */
    @Override
    public void stateChanged(CuratorFramework client, ConnectionState connectionState) {
        if(connectionState == ConnectionState.RECONNECTED){
            client.getCuratorListenable().removeListener(nodeListener);
            client.getCuratorListenable().addListener(nodeListener);
            zookeeperConfigGroup.loadNode();
        }
    }
}

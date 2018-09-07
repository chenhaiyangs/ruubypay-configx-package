package com.ruubypay.framework.configx.zookeeper;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.framework.configx.Encrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 配置组节点
 * @author chenhaiyang
 */
@Slf4j
public class ZookeeperConfigGroup extends AbstractGeneralConfigGroup {
    /**
     * zk配置中心配置Bean
     */
    private ZookeeperConfigProfile configProfile;
    /**
     * 节点名字
     */
    private String node;
    /**
     * zk客户端
     */
    private CuratorFramework client;

    public ZookeeperConfigGroup(ZookeeperConfigProfile configProfile, String node) {
        this(configProfile,node,null);
    }
    public ZookeeperConfigGroup(ZookeeperConfigProfile configProfile, String node, Encrypt encrypt) {
        super(encrypt);
        this.configProfile = configProfile;
        this.node = node;
        this.client=CuratorFrameworkFactory.newClient(configProfile.getConnectStr(),new ExponentialBackoffRetry(1000, 3));
        this.client.start();
        initlistener();
        loadNode();
    }

    /**
     * 监听节点变化的listener
     */
    private CuratorListener nodeListener = new ConfigNodeEventListener(this);
    /**
     * 监听session超时的listener
     */
    private ConnectionStateListener connectListener = new SessionConnectionListener(this,nodeListener);

    /**
     * 初始化监听
     */
    private void initlistener(){
        client.getCuratorListenable().addListener(nodeListener);
        client.getConnectionStateListenable().addListener(connectListener);
    }

    /**
     * 加载group节点并监听
     */
    void loadNode() {

        log.debug("Loading properties for node: {}, with loading mode: {} and keys specified: {}", node, configProfile.getKeyLoadingMode(), configProfile.getKeysSpecified());

        final String nodePath = ZKPaths.makePath(configProfile.getVersionRootNode(), node);
        final GetChildrenBuilder childrenBuilder = client.getChildren();

        try {
            final List<String> children = childrenBuilder.watched().forPath(nodePath);
            if (children != null) {
                final Map<String, String> configs = new HashMap<>(16);
                for (String child : children) {
                    final Pair<String, String> keyValue = loadKey(ZKPaths.makePath(nodePath, child));
                    if (keyValue != null) {
                        configs.put(keyValue.getKey(), keyValue.getValue());
                    }
                }
                cleanAndPutAll(configs);
                //重新将数据放入缓存时也需要更新客户端
                notifyAllKey();
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * 加载某个Key
     * @param nodePath 节点路径
     */
    void reloadKey(final String nodePath) {
        try {
            final Pair<String, String> keyValue = loadKey(nodePath);
            if (keyValue != null) {
                super.put(keyValue.getKey(), keyValue.getValue());
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * 加载某个key
     * @param nodePath key路径
     * @return 返回key-value形式的接口
     * @throws Exception 异常
     */
    private Pair<String, String> loadKey(final String nodePath) throws Exception {

        final String nodeName = ZKPaths.getNodeFromPath(nodePath);
        if (configProfile.canLoadData(nodeName)){
            final GetDataBuilder data = client.getData();
            final String value = new String(data.watched().forPath(nodePath), Charsets.UTF_8);
            return new ImmutablePair<>(nodeName, value);
        }
        return null;
    }

    /**
     * 释放资源
     */
    @PreDestroy
    @Override
    public void close() {
        if (client != null) {
            client.getCuratorListenable().removeListener(nodeListener);
            client.getConnectionStateListenable().removeListener(connectListener);
            client.close();
        }
    }

    @Override
    public boolean set(String key,String value) {
        try {
            return doWrite(key,value);
        } catch (Exception e) {
            log.error("set config err group :{},key:{},value:{}",node,key,value,e);
        }
        return false;
    }

    /**
     * 修改配置中心的值
     * @param key key
     * @param value value
     * @return 返回结果
     * @throws Exception 抛出异常
     */
    private boolean doWrite(String key, String value) throws Exception {
        //配置在配置中心的路径
        final String configNode = ZKPaths.makePath(ZKPaths.makePath(configProfile.getVersionRootNode(), node), key);

        Stat stat = client.checkExists().forPath(configNode);
        if(stat==null){
            String opResult = client.create().creatingParentsIfNeeded().forPath(configNode, value.getBytes(Charsets.UTF_8));
            return Objects.equal(configNode, opResult);
        }else {
            Stat opResult = client.setData().forPath(configNode, value.getBytes(Charsets.UTF_8));
            return opResult != null;
        }
    }
}

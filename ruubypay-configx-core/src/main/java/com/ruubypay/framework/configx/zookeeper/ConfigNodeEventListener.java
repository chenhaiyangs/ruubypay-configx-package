
package com.ruubypay.framework.configx.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.util.Objects;

/**
 * 监听器,监听配置变更
 * 
 * @author chenhaiyang
 *
 */
@Slf4j
public final class ConfigNodeEventListener implements CuratorListener {
    /**
     * 配置组对象
     */
    private final ZookeeperConfigGroup configNode;

	ConfigNodeEventListener(ZookeeperConfigGroup configNode) {
		super();
		this.configNode = Objects.requireNonNull(configNode);
    }

	@Override
	public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
		if (log.isDebugEnabled()) {
            log.debug(event.toString());
		}

		final WatchedEvent watchedEvent = event.getWatchedEvent();
		if (watchedEvent != null) {

            log.debug("Watched event: {}",watchedEvent);
			if (watchedEvent.getState() == KeeperState.SyncConnected) {
				switch (watchedEvent.getType()) {
				case NodeChildrenChanged:
					configNode.loadNode();
					break;
				case NodeDataChanged:
					configNode.reloadKey(watchedEvent.getPath());
					break;
				default:
					break;
				}
			}
		}
	}
}

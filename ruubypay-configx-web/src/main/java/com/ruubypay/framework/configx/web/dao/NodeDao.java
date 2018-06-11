package com.ruubypay.framework.configx.web.dao;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.ruubypay.framework.configx.web.entity.PropertyItem;
import com.ruubypay.framework.configx.web.zkclient.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chenhaiyang
 */
@Component
@Slf4j
public class NodeDao implements INodeDao {

    @Resource
    private ZookeeperClient zookeeperClient;
    /**
     * 根据node加载ZK节点的k-value
     * @param node 节点名
     * @return 返回属性列表
     */
	@Override
	public List<PropertyItem> findProperties(String node) {

		log.debug("Find properties in node: [{}]", node);

		List<PropertyItem> properties = Lists.newArrayList();
		try {
			Stat stat = zookeeperClient.getClient().checkExists().forPath(node);
			if (stat != null) {
				GetChildrenBuilder childrenBuilder = zookeeperClient.getClient().getChildren();
				List<String> children = childrenBuilder.forPath(node);
				GetDataBuilder dataBuilder = zookeeperClient.getClient().getData();
				if (children != null) {
					for (String child : children) {
						String propPath = ZKPaths.makePath(node, child);
						PropertyItem item = new PropertyItem(child, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
						properties.add(item);
					}
				}
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return properties;
	}

    /**
     * 根据节点名获取所有的子节点
     * @param node 节点名
     * @return 返回所有的子节点
     */
	@Override
	public List<String> listChildren(String node) {
		log.debug("Find children of node: [{}]", node);
		List<String> children = null;
		try {
			Stat stat = zookeeperClient.getClient().checkExists().forPath(node);
			if (stat != null) {
				GetChildrenBuilder childrenBuilder = zookeeperClient.getClient().getChildren();
				children = childrenBuilder.forPath(node);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return children;
	}

	@Override
	public String getValue(String node) {
		try {
			// 判断节点是否存在
			Stat stat = zookeeperClient.getClient().checkExists().forPath(node);
			if (stat != null) {
				byte[] data = zookeeperClient.getClient().getData().forPath(node);
				return new String(data);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

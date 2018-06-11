package com.ruubypay.framework.configx.web.dao;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.ruubypay.framework.configx.web.zkclient.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chenhaiyang
 */
@Component
@Slf4j
public class PropertyDao implements IPropertyDao {

    @Resource
    private ZookeeperClient zookeeperClient;

	@Override
	public boolean createProperty(String nodeName, String value) {

		log.debug("Create property : [{}] = [{}]", nodeName, value);

		boolean suc = false;
		try {
			Stat stat = zookeeperClient.getClient().checkExists().forPath(nodeName);
			if (stat == null) {
				final byte[] data = Strings.isNullOrEmpty(value) ? new byte[]{} : value.getBytes(Charsets.UTF_8);
				String opResult = zookeeperClient.getClient().create().creatingParentsIfNeeded().forPath(nodeName, data);
				suc = Objects.equal(nodeName, opResult);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return suc;
	}

	@Override
	public boolean updateProperty(String nodeName, String value) {

		log.debug("Update property: [{}] = [{}]", nodeName, value);
		boolean suc;
		try {
			Stat stat = zookeeperClient.getClient().checkExists().forPath(nodeName);
			if (stat != null) {
				Stat opResult = zookeeperClient.getClient().setData().forPath(nodeName, value.getBytes(Charsets.UTF_8));
				suc = opResult != null;
			} else {
				String opResult = zookeeperClient.getClient().create().creatingParentsIfNeeded().forPath(nodeName, value.getBytes(Charsets.UTF_8));
				suc = Objects.equal(nodeName, opResult);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		return suc;
	}

	@Override
	public void deleteProperty(String nodeName) {

		log.debug("Delete property: [{}]", nodeName);
		try {
			Stat stat = zookeeperClient.getClient().checkExists().forPath(nodeName);
			if (stat != null) {
                zookeeperClient.getClient().delete().deletingChildrenIfNeeded().forPath(nodeName);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

}

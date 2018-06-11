package com.ruubypay.framework.configx.web.service;

import com.ruubypay.framework.configx.web.dao.INodeDao;
import com.ruubypay.framework.configx.web.dao.IPropertyDao;
import com.ruubypay.framework.configx.web.entity.PropertyItem;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author chenhaiyang
 */
@Service
public class NodeServiceImpl implements INodeService, Serializable {

	@Resource
	private INodeDao nodeDao;

	@Resource
	private IPropertyDao propertyDao;

	@Override
	public List<PropertyItem> findProperties(String node) {
		return nodeDao.findProperties(node);
	}

	@Override
	public List<String> listChildren(String node) {
		List<String> children = nodeDao.listChildren(node);
		if (children != null) {
			Collections.sort(children);
		}
		return children;
	}

	@Override
	public boolean createProperty(String nodeName, String value) {
		return propertyDao.createProperty(nodeName, value);
	}

	@Override
	public boolean updateProperty(String nodeName, String value) {
		return propertyDao.updateProperty(nodeName, value);
	}

	@Override
	public void deleteProperty(String nodeName) {
		propertyDao.deleteProperty(nodeName);
	}

	@Override
	public String getValue(String node) {
		return nodeDao.getValue(node);
	}

}

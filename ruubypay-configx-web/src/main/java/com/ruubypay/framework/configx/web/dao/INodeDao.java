package com.ruubypay.framework.configx.web.dao;

import com.ruubypay.framework.configx.web.entity.PropertyItem;

import java.util.List;

/**
 * 节点数据访问
 * 
 * @author chenhaiyang
 *
 */
public interface INodeDao {

	/**
	 * 查找子属性
	 * 
	 * @param node 节点名
	 * @return property item list
	 */
	List<PropertyItem> findProperties(String node);

	/**
	 * 查找子结点
	 * 
	 * @param node 节点名
	 * @return string list
	 */
	List<String> listChildren(String node);

    /**
     * 获取节点的值
     * @param node node名
     * @return 返回值
     */
	String  getValue(String node);
}

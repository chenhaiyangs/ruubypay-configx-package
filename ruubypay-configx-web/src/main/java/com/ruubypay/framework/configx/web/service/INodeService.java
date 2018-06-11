package com.ruubypay.framework.configx.web.service;

import com.ruubypay.framework.configx.web.entity.PropertyItem;

import java.util.List;

/**
 * 配置节点操作Service
 * @author chenhaiyang
 */
public interface INodeService {
	/**
	 * 根据node节点获取key-value
	 * @param node node节点
	 * @return 返回node节点的key-value
	 */
	List<PropertyItem> findProperties(String node);

    /**
     * 列举node下所有的孩子节点
     * @param node node节点
     * @return 返回结果
     */
	List<String> listChildren(String node);

    /**
     * 创建节点
     * @param nodeName 节点名
     * @param value 值
     * @return 返回是否创建成功
     */
	boolean createProperty(String nodeName, String value);

    /**
     * 更新节点
     * @param nodeName 节点名
     * @param value 返回值
     * @return  返回是否更新成功
     */
	boolean updateProperty(String nodeName, String value);

    /**
     * 删除节点
     * @param nodeName 节点名
     */
	void deleteProperty(String nodeName);

    /**
     * 根据节点名获取节点值
     * @param node 节点名
     * @return 返回
     */
	String getValue(String node);
}

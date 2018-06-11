package com.ruubypay.framework.configx.web.dao;

/**
 * 属性的操作
 * 
 * @author chenhaiyang
 *
 */
public interface IPropertyDao {

	/**
	 * 创建属性
	 * @param nodeName 节点
	 * @param value 值
	 * @return 返回 创建结果
	 */
	boolean createProperty(String nodeName, String value);

    /**
     * 更新属性
     * @param nodeName 节点名
     * @param value 值
     * @return 返回创建结果
     */
	boolean updateProperty(String nodeName, String value);

    /**
     * 删除属性
     * @param nodeName 节点名
     */
	void deleteProperty(String nodeName);

}

package com.ruubypay.framework.configx.web.business;

import com.ruubypay.framework.configx.web.entity.PropertyItemVO;

import java.util.List;

/**
 * @author chenhaiyang
 */
public interface INodeBusiness {

	/**
	 * 查询配置项
	 * @param rootNode 根结点
	 * @param version 版本
	 * @param group 组
	 * @return 返回属性列表
	 */
	List<PropertyItemVO> findPropertyItems(String rootNode, String version, String group);

}

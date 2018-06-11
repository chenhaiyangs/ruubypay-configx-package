package com.ruubypay.framework.configx.web.service;

import java.util.List;

/**
 * 授权节点的记录，方便提示
 * 用户输入配置root时，能够提示自动补全
 * @author chenhaiyang
 *
 */
public interface IRootNodeRecorder {
    /**
     * 保存常用配置集
     * @param node 配置根节点
     */
	void saveNode(String node);

    /**
     * 展示常用的配置集
     * @return 返回列表
     */
	List<String> listNode();

}

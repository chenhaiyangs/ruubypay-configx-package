package com.ruubypay.framework.configx.spring.parser;

import com.ruubypay.framework.configx.zookeeper.ZookeeperConfigProfile;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Spring标签解析器
 * @author chenhaiyang
 */
public class ZookeeperConfigProfileConfigBean extends AbstractSingleBeanDefinitionParser {
    /**
     * zk的配置地址xml配置参数名
     */
    private static final String CONNECT_STR="connect-str";
    /**
     * 配置根节点的参数配置名
     */
    private static final String ROOT_NODE="root-node";
    /**
     * 指定引用哪个version的配置
     */
    private static final String VERSION="version";

	@Override
	protected Class<?> getBeanClass(Element element) {
		return ZookeeperConfigProfile.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String connectStr = element.getAttribute(CONNECT_STR);
		builder.addConstructorArgValue(connectStr);
		String rootNode = element.getAttribute(ROOT_NODE);
		builder.addConstructorArgValue(rootNode);
		String version = element.getAttribute(VERSION);
		builder.addConstructorArgValue(version);
	}

}

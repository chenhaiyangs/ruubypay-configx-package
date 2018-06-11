package com.ruubypay.framework.configx.spring.parser;

import com.ruubypay.framework.configx.zookeeper.ZookeeperConfigGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * 解析group的Spring标签解析器
 * @author chenhaiyang
 */
public class ZookeeperConfigGroupConfigBean extends AbstractSingleBeanDefinitionParser {

    /**
     * 指定引用的zk配置类的名称
     */
    private static final String CONFIG_PROFILE_REF="config-profile-ref";
    /**
     * 配置分组的节点名
     */
    private static final String NODE="node";
	/**
	 * 加解密功能
	 */
	private static final String ENCRYPT="encrypt";

	@Override
	protected Class<?> getBeanClass(Element element) {
		return ZookeeperConfigGroup.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {

		String configProfileRef = element.getAttribute(CONFIG_PROFILE_REF);
		builder.addConstructorArgReference(configProfileRef);
		String node = element.getAttribute(NODE);
		builder.addConstructorArgValue(node);
		String encrypt = element.getAttribute(ENCRYPT);
		if(StringUtils.isNotBlank(encrypt)){
			builder.addConstructorArgReference(encrypt);
		}
	}

}

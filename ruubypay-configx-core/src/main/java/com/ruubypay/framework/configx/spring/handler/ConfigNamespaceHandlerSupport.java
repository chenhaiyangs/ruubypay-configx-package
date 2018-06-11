package com.ruubypay.framework.configx.spring.handler;

import com.ruubypay.framework.configx.spring.parser.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 向Spring注册配置中心标签
 * @author chenhaiyang
 */
public class ConfigNamespaceHandlerSupport extends NamespaceHandlerSupport {
    /**
     * zk配置类文件解析标签
     */
	private static final String PROFILE="profile";
    /**
     * 配置组文件解析标签
     */
	private static final String GROUP="group";
    /**
     * 内部生成配置Bean的解析标签
     */
    private static final String INNERBEAN="innerbean";
    /**
     * 外部生成的配置Bean的解析标签
     */
    private static final String OUTERBEAN="outerbean";
    /**
     * 使用AES进行加密
     */
    private static final String AES="encrypt-by-aes";
    /**
     * 使用DES进行加密
     */
    private static final String DES="encrypt-by-des";


	@Override
	public void init() {
		registerBeanDefinitionParser(PROFILE, new ZookeeperConfigProfileConfigBean());
		registerBeanDefinitionParser(GROUP, new ZookeeperConfigGroupConfigBean());
		registerBeanDefinitionParser(INNERBEAN, new InnerBeanBindConfigBean());
		registerBeanDefinitionParser(OUTERBEAN, new OuterBeanBindConfigBean());
		registerBeanDefinitionParser(AES, new AesEncryptConfigBean());
		registerBeanDefinitionParser(DES, new DesEncryptConfigBean());
	}

}

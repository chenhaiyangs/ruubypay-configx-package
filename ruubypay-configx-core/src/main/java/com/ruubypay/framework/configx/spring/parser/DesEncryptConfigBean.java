package com.ruubypay.framework.configx.spring.parser;

import com.ruubypay.framework.configx.encrypt.impl.EncryptByDes;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * 使用Des算法对配置中心的配置进行加密
 * @author chenhaiyang
 */
public class DesEncryptConfigBean extends AbstractSingleBeanDefinitionParser {

    /**
     * 密钥
     */
    private static final String KEY="key";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return EncryptByDes.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String key = element.getAttribute(KEY);
        builder.addConstructorArgValue(key);
    }
}

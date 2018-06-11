package com.ruubypay.framework.configx.spring.parser;

import com.ruubypay.framework.configx.bean.ConfigReflectWithInnerBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * innerbean标签解析器
 * @author chenhaiyang
 */
public class InnerBeanBindConfigBean extends AbstractSingleBeanDefinitionParser {

    /**
     * 绑定配置Bean的类元信息的类路径
     */
    private static final String BIND_CLASS="bind-class";
    /**
     * 配置分组的节点名
     */
    private static final String NODE="node";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ConfigReflectWithInnerBean.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String bindClass = element.getAttribute(BIND_CLASS);
        builder.addConstructorArgValue(bindClass);
        String node = element.getAttribute(NODE);
        builder.addConstructorArgReference(node);
    }
}

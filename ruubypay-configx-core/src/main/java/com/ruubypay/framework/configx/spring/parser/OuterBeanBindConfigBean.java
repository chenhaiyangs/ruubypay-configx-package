package com.ruubypay.framework.configx.spring.parser;

import com.ruubypay.framework.configx.bean.ConfigReflectWithOuterBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * 外部绑定配置Bean outerbean标签解析器
 * @author chenhaiyang
 */
public class OuterBeanBindConfigBean extends AbstractSingleBeanDefinitionParser {

    /**
     * 绑定配置Bean的实例
     */
    private static final String BIND_REF="bind-ref";
    /**
     * 配置分组的节点名
     */
    private static final String NODE="node";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ConfigReflectWithOuterBean.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String bindBean = element.getAttribute(BIND_REF);
        builder.addConstructorArgReference(bindBean);
        String node = element.getAttribute(NODE);
        builder.addConstructorArgReference(node);
    }
}



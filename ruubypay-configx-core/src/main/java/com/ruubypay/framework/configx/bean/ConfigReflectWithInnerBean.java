package com.ruubypay.framework.configx.bean;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;

import java.util.List;

/**
 * 内部根据类的元信息生成实例,即配置Bean内部维护
 * @param <T> 配置对象类型
 * @author chenhaiyang
 */
public class ConfigReflectWithInnerBean<T> extends ConfigReflectWithBean<T>{

    public ConfigReflectWithInnerBean(Class<T> configClass, AbstractGeneralConfigGroup node, List<String> carekeys) throws Exception {
        super(configClass,node,carekeys);
    }
    public ConfigReflectWithInnerBean(Class<T> configClass, AbstractGeneralConfigGroup node) throws Exception {
        super(configClass,node);
    }
}

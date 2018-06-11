package com.ruubypay.framework.configx.bean;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;

import java.util.List;

/**
 * 与外部提供的配置Bean绑定配置。
 * @param <T> 配置对象类型
 * @author chenhaiyang
 */
public class ConfigReflectWithOuterBean<T> extends ConfigReflectWithBean<T>{

    public ConfigReflectWithOuterBean(T config, AbstractGeneralConfigGroup node, List<String> carekeys) {
        super(config, node, carekeys);
    }
    public ConfigReflectWithOuterBean(T config, AbstractGeneralConfigGroup node){
        super(config,node);
    }

}

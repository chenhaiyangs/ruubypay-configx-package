package com.ruubypay.framework.configx.bean;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.framework.configx.BaseConfigWithBean;
import com.ruubypay.framework.configx.bean.annotation.ProperKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * 一个通用的默认的配置Bean的实现。使用java反射实现属性绑定
 * @param <T> 要绑定的Bean
 * @author chenhaiyang
 */
@Slf4j
public class ConfigReflectWithBean<T> extends BaseConfigWithBean<T> {

    ConfigReflectWithBean(T config, AbstractGeneralConfigGroup node, List<String> carekeys) {
        super(config,node, carekeys);
    }
    ConfigReflectWithBean(T config, AbstractGeneralConfigGroup node){
        super(config,node);
    }
    ConfigReflectWithBean(Class<T> configClass, AbstractGeneralConfigGroup node,List<String> carekeys) throws Exception {
        super(configClass,node,carekeys);
    }
    ConfigReflectWithBean(Class<T> configClass, AbstractGeneralConfigGroup node) throws Exception {
        super(configClass,node);
    }

    /**
     * 类型转换函数注册接口
     */
    private static Map<Class,Function<String,?>> typeRegister = new HashMap<>();
    static {
        typeRegister.put(String.class,String::valueOf);
        typeRegister.put(Integer.class,Integer::valueOf);
        typeRegister.put(int.class,Integer::valueOf);
        typeRegister.put(Boolean.class,Boolean::valueOf);
        typeRegister.put(boolean.class,Boolean::valueOf);
        typeRegister.put(Long.class,Long::valueOf);
        typeRegister.put(long.class,Long::valueOf);
        typeRegister.put(Double.class,Double::valueOf);
        typeRegister.put(double.class,Double::valueOf);
        typeRegister.put(Float.class,Float::valueOf);
        typeRegister.put(float.class,Float::valueOf);
    }

    /**
     * 设置企业
     * @param field 字段
     * @param node 节点
     * @param config 配置
     */
    private void setFieid(Field field,AbstractGeneralConfigGroup node, T config){
        try {
            field.setAccessible(true);
            String key= getKetByField(field);
            String value = node.get(key);
            if(value!=null){
                value=value.trim();
                Class clazz = field.getType();
                setFieid(field,value,config,typeRegister.get(clazz));
            }
            log.debug("reload fieldName: [{}], key:[{}],value:[{}]",field.getName(),key,value);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 设置属性值
     * @param field 字段
     * @param value String 类型的值
     * @param config 被反射的类
     * @param stringFunction 转化表达式
     */
    private void setFieid(Field field,String value,T config, Function<String, ?> stringFunction) throws IllegalAccessException {
        field.set(config,stringFunction.apply(value));
    }

    /**
     * 根据字段值和注解获取配置中的key
     * @param field 字段
     * @return 返回key
     */
    private String getKetByField(Field field){

        String keyName = field.getName();
        ProperKey properKey = field.getAnnotation(ProperKey.class);
        if(properKey!=null){
           String prokeyName = properKey.key();
           if(StringUtils.isNotBlank(prokeyName)){
                keyName=prokeyName.trim();
           }
        }
        return keyName;
    }

    /**
     * 利用java反射机制实现刷新配置。标准javaBean
     * @param node node节点
     * @param config 要绑定的configbean
     */
    @Override
    protected void doRefreshBean(AbstractGeneralConfigGroup node, T config) {
        Field[] fields = config.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> setFieid(field,node,config));
        log.info(" get new bean --> {}",config.toString());
    }
}

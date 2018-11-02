package com.ruubypay.framework.configx.proxy;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.framework.configx.bean.annotation.ProperKey;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 使用cglib动态代理javaBean实现配置中心的修改
 * @author chenhaiyang
 * @param <T> 被代理的对象
 */
@SuppressWarnings("all")
@Slf4j
public class ConfigBeanProxy<T> implements MethodInterceptor{
    /**
     * set 方法
     */
    private static final String SET="set";
    /**
     * 被代理对象
     */
    private T target;
    /**
     * 配置组
     */
    private AbstractGeneralConfigGroup node;

    private Enhancer enhancer = new Enhancer();

    public ConfigBeanProxy(T target, AbstractGeneralConfigGroup node) {
        this.target = target;
        this.node=node;
    }

    public T getProxy(){
        // 设置代理目标
        enhancer.setSuperclass(target.getClass());
        // 设置回调
        enhancer.setCallback(this);
        return (T)enhancer.create();

    }

    /**
     * 代理javaBean实现调用set方法则修改配置中心的值
     * @param proxy 代理
     * @param method 真实的method
     * @param args 参数
     * @param methodProxy 方法代理
     * @return 执行结果返回
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //调用set方法且set方法有值。且是public 的方法 ：标准javaBean
        if(method.getName().startsWith(SET)
                && Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 1){


            String fieldName = method.getName().substring(SET.length(),SET.length()+1).toLowerCase()+ method.getName().substring(SET.length()+1);
            Field field = target.getClass().getDeclaredField(fieldName);
            if(field!=null){
                String keyName = field.getName();
                //是否需要加密
                boolean needEncrypt = false;
                ProperKey properKey = field.getAnnotation(ProperKey.class);
                if(properKey!=null){
                    String prokeyName = properKey.key();
                    needEncrypt= properKey.needEncrypt();
                    if(StringUtils.isNotBlank(prokeyName)){
                        keyName=prokeyName.trim();
                    }
                }
                String value = args[0].toString();
                if(needEncrypt){
                    value = node.encryptValue(value);
                }
                if(!node.set(keyName,value)){
                    log.warn("set key:{},value:{},fail",keyName,value);
                }
            }
            return null;

        }else{
            return method.invoke(target,args);
        }
    }
}

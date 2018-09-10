package com.ruubypay.framework.configx.bean.annotation;

import java.lang.annotation.*;

/**
 * javaBean的Field与配置组的配置key形成映射的一个注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
@Inherited
@Documented
public @interface ProperKey {

    /**
     * key名称
     * @return 返回字段对应的ConfigGroup的配置的key
     */
    String key() default "";

    /**
     * 是否需要加密，默认为false
     * @return 是否需要加密
     */
    boolean needEncrypt() default false;
}

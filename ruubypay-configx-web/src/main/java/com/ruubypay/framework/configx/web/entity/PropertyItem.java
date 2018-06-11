package com.ruubypay.framework.configx.web.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 属性配置项
 * @author chenhaiyang
 */
@Data
public class PropertyItem implements Serializable {

	private static final long serialVersionUID = 1L;
    /**
     * 属性key
     */
	private String name;
    /**
     * 属性value
     */
	private String value;

    public PropertyItem(String name, String value) {
        this.name = name;
        this.value = value;
    }
}

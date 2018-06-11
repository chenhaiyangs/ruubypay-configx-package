package com.ruubypay.framework.configx.web.entity;
import lombok.Data;

import java.io.Serializable;

/**
 * 属性VO类
 * @author chenhaiyang
 */
@Data
public class PropertyItemVO implements Serializable, Comparable<PropertyItemVO> {

	private static final long serialVersionUID = 1L;
    /**
     * key
     */
	private String oriName;
    /**
     * key
     */
	private String name;
    /**
     * value
     */
	private String value;
    /**
     * 注释
     */
	private String comment;

    public PropertyItemVO(PropertyItem propertyItem) {
        super();
        this.name = propertyItem.getName();
        this.oriName = propertyItem.getName();
        this.value = propertyItem.getValue();
    }
	public PropertyItemVO(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * (non-Javadoc)
	 *
	 */
	@Override
	public int compareTo(PropertyItemVO o) {
		return this.name.compareTo(o.getName());
	}

}

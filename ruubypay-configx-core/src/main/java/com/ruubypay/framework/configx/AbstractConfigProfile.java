package com.ruubypay.framework.configx;

import lombok.Data;

import java.util.Objects;
import java.util.Set;

/**
 * 配置组参数
 * @author chenhaiyang
 */
@Data
public abstract class AbstractConfigProfile {

    /**
	 * 节点下属性的加载模式
	 * @author chenhaiyang
	 *
	 */
	public enum KeyLoadingMode {
		/**
		 * 加载所有属性
		 */
		ALL,
		/**
		 * 只加载包含的某些属性
		 */
		INCLUDE,
		/**
		 * 排除某些属性
		 */
		EXCLUDE
	}
    /**
     * 配置文件加载方式，默认是加载全部属性
     */
    private KeyLoadingMode keyLoadingMode = KeyLoadingMode.ALL;
    /**
     * 需要包含或排除的key,由{@code KeyLoadingMode}决定
     */
    private Set<String> keysSpecified;

	/**
	 * 项目配置版本
	 */
	protected final String version;

	public AbstractConfigProfile(String version) {
		super();
        this.version = Objects.requireNonNull(version);
	}

    /**
     * 是否可以加载该节点数据
	 * @param  nodeName 节点名称
     * @return 是／否
     */
    public boolean canLoadData(String nodeName){
        switch (this.getKeyLoadingMode()) {
            case INCLUDE:
                //如果加载模式是包含，则只有可以在keysSpecified中配置的，才允许加载
                if (keysSpecified == null || !keysSpecified.contains(nodeName)) {
                    return false;
                }
                //如果加载模式是排除某些属性，则在keysSpecified中配置的属性不能加载
            case EXCLUDE:
                if (keysSpecified.contains(nodeName)) {
                    return false;
                }
            case ALL:
                return true;
            default:
                return true;
        }
    }

}

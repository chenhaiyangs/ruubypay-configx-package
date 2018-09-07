package com.ruubypay.framework.configx;

import com.ruubypay.framework.configx.observer.ISubject;

import java.io.Closeable;
import java.util.Map;

/**
 * 配置组扩展接口
 * @author chenhaiyang
 */
public interface ConfigGroup extends Map<String, String>, Closeable, ISubject {
    /**
     * 获取配置
     * @param key key
     * @return value
     */
	String get(String key);

    /**
     * 更新配置
     * @param key key key
     * @param value value value
     * @return 返回结果，是否更新成功
     */
	boolean set(String key,String value);
	
}

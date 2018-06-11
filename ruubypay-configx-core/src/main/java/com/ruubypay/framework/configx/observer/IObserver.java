package com.ruubypay.framework.configx.observer;

/**
 * 观察者数据类型
 * @author chenhaiyang
 */
public interface IObserver {

	/**
	 * 观察者收到通知
	 * @param key 配置key
	 * @param value 数据
	 */
	void notifyObserver(String key, String value);
    /**
     * 观察者收到通知,reload所有的key
     */
	void notifyObserver();

}

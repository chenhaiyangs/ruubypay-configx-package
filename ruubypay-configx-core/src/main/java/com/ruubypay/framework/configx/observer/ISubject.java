
package com.ruubypay.framework.configx.observer;

/**
 * 被观察者关心的主题
 * @author chenhaiyang
 */
public interface ISubject {

	/**
	 * 注册观察者
	 * @param watcher 观察者
	 */
	void register(IObserver watcher);

	/**
	 * 通知观察者
	 * @param key 属性key
	 * @param value 属性value
	 */
	void notify(String key, String value);

	/**
	 * 所有的key都更新，通知观察者拉取最新配置
	 */
	void notifyAllKey();

}

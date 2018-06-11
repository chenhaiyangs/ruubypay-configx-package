package com.ruubypay.framework.configx;

import com.ruubypay.framework.configx.observer.IObserver;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 以javaBean形成的热配置，javaBean字段和configgroup的key形成一一映射。
 * @author chenhaiyang
 */
public abstract class BaseConfigWithBean<T> implements IObserver{

    /**
     * 真实配置Bean
     */
    private final T config;
    /**
     * 会影响真实对象的属性值，为空时代表任意属性变化都会刷新对象
     */
    private List<String> carekeys;
    /**
     * 配置组
     */
    private AbstractGeneralConfigGroup node;
    /**
     * 读写锁
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected BaseConfigWithBean(final T config,AbstractGeneralConfigGroup node, List<String> carekeys) {
        this.node = Objects.requireNonNull(node);
        this.carekeys = carekeys;
        this.config=config;

        node.register(this);
        refreshBean();
    }

    protected BaseConfigWithBean(Class<T> configClass,AbstractGeneralConfigGroup node, List<String> carekeys) throws Exception {
        this.node = Objects.requireNonNull(node);
        this.carekeys = carekeys;
        this.config=configClass.newInstance();

        node.register(this);
        refreshBean();
    }
    /**
     * 和外部提供的配置Bean绑定配置。应用需要自己实现config的配置初始化，这里只保证这个Bean的配置热更新，不保证业务方调用时初始化。
     * @param config 配置类
     * @param node 配置组
     */
    protected BaseConfigWithBean(final T config,AbstractGeneralConfigGroup node) {
        this(config,node, null);
    }

    /**
     * 自己内部生产配置Bean和参数绑定，管理Bean的初始化和热更新。但框架API对业务侵入比较大
     * @param configClass 需要实例化的类的元信息
     * @param node 配置组
     * @throws Exception 反射异常
     */
    protected BaseConfigWithBean(Class<T> configClass,AbstractGeneralConfigGroup node) throws Exception {
        this(configClass,node, null);
    }

    /**
     * 刷新绑定的Bean
     */
    private void refreshBean() {
        lock.writeLock().lock();
        try {
            doRefreshBean(node,config);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取配置javaBean，获取的是BaseConfigWithBean根据配置类的元信息自己内部生成的Bean
     * @return 配置类
     */
    public T getConfigBean(){
        return config;
    }
    /**
     * 模版方法 自己实现的和Bean类型的配置实现动态绑定的逻辑
     * @param node node节点
     * @param obj 要绑定的bean
     */
    protected abstract void doRefreshBean(AbstractGeneralConfigGroup node,T obj);

    /**
     * 观察者模式，当配置变更时触发bean属性的变更
     * @param key 配置key
     * @param value 数据
     */
    @Override
    public void notifyObserver(String key, String value) {
        if (carekeys == null || carekeys.isEmpty() || carekeys.contains(key)) {
            refreshBean();
        }
    }

    /**
     * 观察者模式，当配置变更时触发bean属性的变更。此通知要求观察者reload所有的配置
     */
    @Override
    public void notifyObserver() {
        refreshBean();
    }
}

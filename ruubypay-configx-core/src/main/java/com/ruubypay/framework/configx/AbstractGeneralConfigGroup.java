package com.ruubypay.framework.configx;

import com.ruubypay.framework.configx.observer.IObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置组抽象实现。
 * @author chenhaiyang
 */
@Slf4j
public abstract class AbstractGeneralConfigGroup extends ConcurrentHashMap<String, String> implements ConfigGroup {

    /**
     * 判断配置是否需要解密
     */
    private static final String NEED_DECRYPT="{cipher}";

    /**
     * 加密接口
     */
    private Encrypt encrypt;
    /**
     * 本地配置->用于在集群中调试单点。
     * 如果overrideLocalConfig 有值，则不使用配置中心的值
     */
    private AbstractGeneralConfigGroup overrideLocalConfig;

    protected void setOverrideLocalConfig(AbstractGeneralConfigGroup overrideLocalConfig){
        this.overrideLocalConfig=overrideLocalConfig;
    }

    protected AbstractGeneralConfigGroup(){}
    protected AbstractGeneralConfigGroup(Encrypt encrypt){
        this.encrypt=encrypt;
    }

    /**
     * 根据key获取配置。
     * @param key key
     * @return 返回配置
     */
	@Override
	public final String get(String key){
       return super.get(key);
	}

    /**
     * 重载函数，根据object类型的key获取配置
     * @param key jey
     * @return 返回配置
     */
	@Override
	public final String get(Object key) {
		return get(key.toString());
	}

    /**
     * set 即为设置配置中心的key的值，由于存储介质不同，需要具体的子类去实现该函数
     * @param key key key
     * @param value value value
     * @return set success
     */
    @Override
    public boolean set(String key,String value) {
       throw new UnsupportedOperationException();
    }

    /**
     * 对value进行加密
     * @param value 要加密的值
     * @return 返回加密后的结果。可以直接在配置中心存储的格式，如 {cipher}xxxxxxx
     * @throws Exception 异常
     */
    public String encryptValue(String value) throws Exception {
        if(this.encrypt==null){
            return value;
        }
        return String.format("%s%s",NEED_DECRYPT,encrypt.encrypt(value));
    }

    /**
     * 根据key删除配置
     * @param key key
     */
    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * clear整个配置
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * 向本地配置组添加配置: 处理加解密逻辑和本地配置覆盖的逻辑
     * @param key key
     * @param value value
     * @return 返回上一次的配置值
     */
    @Override
    public final String put(String key, String value) {

        value = Objects.requireNonNull(value).trim();
        String preValue = super.get(key);

        if (!Objects.equals(preValue, value)) {
            log.debug("Key {} change from {} to {}", key, preValue, value);

            String resultValue;
            if(overrideLocalConfig!=null){
                resultValue = overrideLocalConfig.get(key);
                //如果本地无此配置，仍然沿用配置中心的配置
                if(resultValue==null){
                    resultValue=value;
                }
            }else{
                resultValue=value;
            }
            //如果这个value是加密过的，直接在存储到本地map时就解密
            if(resultValue.startsWith(NEED_DECRYPT)&& encrypt!=null){
                resultValue = resultValue.substring(NEED_DECRYPT.length(),resultValue.length());
                try {
                    resultValue=encrypt.decrypt(resultValue);
                } catch (Exception e) {
                    log.error("decrtpt key:{} value:{},err",key,value,e);
                }
            }
            super.put(key, resultValue);
            //如果值变了，通知观察者
            if (preValue != null) {
                notify(key, resultValue);
            }
        }
        return preValue;
    }


    /**
     * 清除本地配置，重新载入最新的配置
     * @param configs 最新的配置集
     */
	protected final void cleanAndPutAll(Map<String,String> configs) {

	    if(configs==null||configs.size()==0){
            log.debug("bean group has none keys, clear.");
            super.clear();
            return;
        }
        //config中不包含，但本地包含的key，需要清除掉
        if (this.size() > 0) {
            final Set<String> newKeys =configs.keySet();
            this.keySet()
                    .stream()
                    .filter(input->!newKeys.contains(input))
                    .forEach(super::remove);
        }
        //重新将新的配置 写入缓存
        configs.forEach(this::put);
	}


	/**
	 * 观察者列表
	 */
	private final List<IObserver> watchers = new ArrayList<>();
    /**
     * 注册成为一个观察者，当配置变更后收到通知
     * @param watcher 观察者
     */
	@Override
	public void register(final IObserver watcher) {
		watchers.add(Objects.requireNonNull(watcher));
	}
    /**
     * 通知所有的观察者，配置已经变更
     * @param key 属性key
     * @param value 属性value
     */
	@Override
	public void notify(final String key, final String value) {
	    watchers.forEach(watcher->
	        new Thread(()->watcher.notifyObserver(key, value)).start()
        );
	}

    /**
     * 通知所有的观察者，配置已经变更，需要重新reload所有配置
     */
    @Override
    public void notifyAllKey() {
        watchers.forEach(watcher->
                new Thread(watcher::notifyObserver).start()
        );
    }
}

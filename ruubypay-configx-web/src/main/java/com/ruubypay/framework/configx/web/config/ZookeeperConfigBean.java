package com.ruubypay.framework.configx.web.config;

import com.ruubypay.framework.configx.web.zkclient.ZookeeperClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化zookeeper客户端
 * @author chenhaiyang
 */
@Configuration
public class ZookeeperConfigBean {

    @Value("${zk}")
    private String zookeeperUrl;

    @Bean
    public ZookeeperClient getZookeeperClient(){
        return new ZookeeperClient(zookeeperUrl);
    }
}

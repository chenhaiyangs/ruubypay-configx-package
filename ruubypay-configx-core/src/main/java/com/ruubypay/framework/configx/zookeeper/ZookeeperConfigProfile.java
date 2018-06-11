
package com.ruubypay.framework.configx.zookeeper;

import com.ruubypay.framework.configx.AbstractConfigProfile;
import lombok.Getter;
import org.apache.curator.utils.ZKPaths;

import java.util.Objects;

/**
 * 配置中心的基本配置
 * @author chenhaiyang
 */
public class ZookeeperConfigProfile extends AbstractConfigProfile {

    /**
     * zk连接地址
     */
    @Getter
    private final String connectStr;

    /**
     * 项目配置根目录
     */
    @Getter
    private final String rootNode;

    public ZookeeperConfigProfile(final String connectStr, final String rootNode, final String version) {
        super(version);
        this.connectStr =Objects.requireNonNull(connectStr);
        this.rootNode = Objects.requireNonNull(rootNode);
    }

    /**
     * 获取versionlNode的zk路径
     * @return 返回version路径
     */
    String getVersionRootNode() {
        return ZKPaths.makePath(rootNode, version);
    }

}

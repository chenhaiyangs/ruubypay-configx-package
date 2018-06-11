package com.ruubypay.framework.configx.web.util;

import org.apache.curator.utils.ZKPaths;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring sceturyUtil 相关工具类
 * @author chenhaiyang
 */
public class SecturyUtil {

    /**
     * 获取root节点
     * @return 返回root节点。即用户名
     */
    public static String getRoot() {
        final UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}

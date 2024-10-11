package com.superb.common.security.properties;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-26 08:35
 */
@Data
public class SecurityProperties {

    /**
     * 全局白名单，忽略租户与客户端
     */
    private Set<String> ignoreUrl = new HashSet<>();

    /**
     * 子服务忽略白名单，不忽略租户与客户端
     */
    private Set<String> ignoreServiceUrl = new HashSet<>();

    /**
     * 排除黑名单
     */
    private Set<String> ips = new HashSet<>();

}

package com.superb.common.database.config.properties;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-26 13:28
 */
@Data
public class TableProperties {

    /**
     * 租户忽略拦截
     */
    private Set<String> ignoreTenantTable = new HashSet<>();

    /**
     * 删除忽略拦截
     */
    private Set<String> ignoreDelTable = new HashSet<>();

    /**
     * 删除忽略拦截
     */
    private Set<String> ignoreOrgan = new HashSet<>();

    /**
     * 租户忽略
     */
    private String baseTenant = "superb";

    /**
     * 字段索引校验
     */
    private Set<String> indexCheckColumns = new HashSet<>();

}

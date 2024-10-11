package com.superb.common.security.entity;

import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-12 13:11
 */
@Data
public class SuperbUserDataScope {

    /**
     * 数据权限范围
     * 0本人；1全部；2本部门；3本部门及子部门；4自定义
     */
    private Integer dataScopeType;

    /**
     * 自定义部门权限
     */
    private String dataScopeOrganId;

    /**
     * 自定义部门权限范围
     * 0本部门；1本部门及子部门
     */
    private Integer dataScopeOrganType;

}

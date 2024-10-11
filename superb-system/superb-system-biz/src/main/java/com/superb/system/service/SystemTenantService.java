package com.superb.system.service;

import com.mybatisflex.core.service.IService;
import com.superb.system.api.entity.SystemTenant;

/**
 * 系统租户管理表;(system_tenant)表服务接口
 * @Author: ajie
 * @CreateTime: 2024-5-11
 */
public interface SystemTenantService extends IService<SystemTenant> {
    String getIndex(String table, String column);

    Boolean getExistColumn(String table, String column);
}
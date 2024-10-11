package com.superb.system.service.impl;

import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.system.api.entity.SystemTenant;
import com.superb.system.mapper.SystemTenantMapper;
import com.superb.system.service.SystemTenantService;
import org.noear.solon.annotation.Component;

/**
 * 系统租户管理表;(system_tenant)表服务实现类
 * @Author: ajie
 * @CreateTime: 2024-5-11
 */
@Component
public class SystemTenantServiceImpl extends ServiceImpl<SystemTenantMapper, SystemTenant> implements SystemTenantService {

    @Override
    public String getIndex(String table, String column) {
        return this.mapper.getIndex(table, column);
    }

    @Override
    public Boolean getExistColumn(String table, String column) {
        return this.mapper.getExistColumn(table, column);
    }
}
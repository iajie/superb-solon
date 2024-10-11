package com.superb.system.service.impl;

import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.common.utils.HeadersUtils;
import com.superb.system.api.entity.SystemRole;
import com.superb.system.mapper.SystemRoleMapper;
import com.superb.system.service.SystemRoleService;
import org.noear.solon.annotation.Component;

import java.util.List;

/**
 * 系统角色表;(system_role)表服务实现类
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Component
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRole> implements SystemRoleService {
    @Override
    public List<String> listByUserId(String userId) {
        String list = this.mapper.listByUserId(userId, HeadersUtils.getOrganizationId());
        return List.of(list.split(","));
    }

}
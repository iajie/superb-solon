package com.superb.common.security.service;

import cn.dev33.satoken.stp.StpInterface;
import com.superb.common.security.utils.SuperbUtils;
import org.noear.solon.annotation.Component;

import java.util.List;

/**
 * sa-token获取角色和权限列表
 * @Author: ajie
 * @CreateTime: 2024-07-26 08:56
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return SuperbUtils.permissionList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return SuperbUtils.roleList();
    }
}

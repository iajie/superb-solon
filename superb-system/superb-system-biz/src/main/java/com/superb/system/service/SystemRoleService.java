package com.superb.system.service;

import com.mybatisflex.core.service.IService;
import com.superb.system.api.entity.SystemRole;

import java.util.List;

/**
 * 系统角色表;(system_role)表服务接口
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
public interface SystemRoleService extends IService<SystemRole> {

    List<String> listByUserId(String userId);
}
package com.superb.system.service;

import com.mybatisflex.core.service.IService;
import com.superb.system.api.dto.DataScope;
import com.superb.system.api.entity.UserDataScope;

import java.util.List;

/**
 * 用户数据权限范围;(system_user_data_scope)表服务接口
 * @Author: ajie
 * @CreateTime: 2024-7-3
 */
public interface UserDataScopeService extends IService<UserDataScope> {

    List<DataScope> listByUserId(String userId);
}
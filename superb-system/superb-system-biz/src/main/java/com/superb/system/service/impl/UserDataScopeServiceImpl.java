package com.superb.system.service.impl;

import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.system.api.dto.DataScope;
import com.superb.system.api.entity.UserDataScope;
import com.superb.system.mapper.UserDataScopeMapper;
import com.superb.system.service.UserDataScopeService;
import org.noear.solon.annotation.Component;

import java.util.List;

/**
 * 用户数据权限范围;(system_user_data_scope)表服务实现类
 * @Author: ajie
 * @CreateTime: 2024-7-3
 */
@Component
public class UserDataScopeServiceImpl extends ServiceImpl<UserDataScopeMapper, UserDataScope> implements UserDataScopeService {
    @Override
    public List<DataScope> listByUserId(String userId) {
        return this.mapper.dataScopes(userId);
    }
}
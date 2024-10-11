package com.superb.system.rpc;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.security.entity.Organization;
import com.superb.common.security.entity.SuperbTenant;
import com.superb.common.security.entity.SuperbUser;
import com.superb.common.security.entity.SuperbUserDataScope;
import com.superb.common.security.feign.RemoteSecuritySystemService;
import com.superb.common.utils.HeadersUtils;
import com.superb.system.api.entity.SystemTenant;
import com.superb.system.api.entity.UserDataScope;
import com.superb.system.service.*;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 远程实现
 * @Author: ajie
 * @CreateTime: 2024-07-31 10:00
 */
@Remoting
@Mapping("/rpc/security")
public class SecuritySystemController implements RemoteSecuritySystemService {

    @Inject
    private SystemTenantService tenantService;
    @Inject
    private SystemUserService userService;
    @Inject
    private SystemRoleService roleService;
    @Inject
    private SystemPermissionService permissionService;
    @Inject
    private SystemOrganizationService organizationService;
    @Inject
    private UserDataScopeService userDataScopeService;

    @Override
    public Result<SuperbTenant> getTenantInfo() {
        RedisKey key = new RedisKey(KeyType.TIME, Duration.ofHours(5),"info");
        SuperbTenant superbTenant = RedisUtils.build().value().getNullSet(key, () -> {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(SystemTenant::getTenantKey, HeadersUtils.getTenantId());
            return tenantService.getOneAs(queryWrapper, SuperbTenant.class);
        }, (redisUtils) -> redisUtils.value().get(key));
        return Result.success(superbTenant);
    }

    @Override
    public Result<SuperbUser> getUserById(String id) {
        RedisKey key = new RedisKey(KeyType.PER, "userInfo:" + id);
        SuperbUser superbUser = RedisUtils.build().hash().getNullSet(key, "info", () -> userService.getInfoById(id),
                (redisUtils) -> redisUtils.hash().get(key, "info", SuperbUser.class));
        return Result.success(superbUser);
    }

    @Override
    public Result<List<String>> getUserRoleList(String id) {
        RedisKey key = new RedisKey(KeyType.PER, "userInfo:" + id + ":organId:" + HeadersUtils.getOrganizationId());
        List<String> roles = RedisUtils.build().hash().getNullSet(key, "role", () -> roleService.listByUserId(id),
                (redisUtils) -> redisUtils.hash().getArray(key, "role", String.class));
        return Result.success(roles);
    }

    @Override
    public Result<List<String>> getUserPermissionList(String userId) {
        RedisKey key = new RedisKey(KeyType.PER, "userInfo:" + userId + ":organId:" + HeadersUtils.getOrganizationId());
        List<String> permissions = RedisUtils.build().hash().getNullSet(key, "permissions", () -> permissionService.listByUserId(userId),
                (redisUtils) -> redisUtils.hash().getArray(key, "permissions", String.class));
        return Result.success(permissions);
    }

    @Override
    public Result<Organization> getOrganization() {
        RedisKey key = new RedisKey(KeyType.TIME, Duration.ofHours(5),"organization:" + HeadersUtils.getOrganizationId());
        Organization organization = RedisUtils.build().value().getNullSet(key, () -> organizationService.getById(HeadersUtils.getOrganizationId()),
                (redisUtils) -> redisUtils.value().get(key));
        return Result.success(organization);
    }

    @Override
    public Result<List<String>> getOrganizationIds(String organId) {
        RedisKey key = new RedisKey(KeyType.TIME, Duration.ofHours(5),"organizationIds:" + organId);
        List<String> organizationIds = RedisUtils.build().value().getNullSet(key, () -> {
            List<String> list = new ArrayList<>();
            list.add(organId);
            organizationService.getIdAndSub(list, organId);
            return list;
        }, (redisUtils) -> redisUtils.value().get(key));
        return Result.success(organizationIds);
    }

    @Override
    @SuperbDataScope
    public Result<SuperbUserDataScope> getUserDataScope() {
        String userId = StpUtil.getLoginIdAsString();
        String organizationId = HeadersUtils.getOrganizationId();
        RedisKey key = new RedisKey(KeyType.PER, "userInfo:" + userId + ":organId:" + organizationId);
        SuperbUserDataScope dataScope = RedisUtils.build().hash().getNullSet(key, "dataScope", () -> {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.select("data_scope_type", "data_scope_organ_id", "data_scope_organ_type");
            queryWrapper.eq(UserDataScope::getUserId, userId).eq(UserDataScope::getOrganId, organizationId);
            return userDataScopeService.getOneAs(queryWrapper, SuperbUserDataScope.class);
        }, redis -> redis.hash().get(key, "dataScope"));
        return Result.success(dataScope);
    }

    @Override
    public Result<String> getDatabaseIndex(String table, String column) {
        return Result.success("", tenantService.getIndex(table, column));
    }

    @Override
    public Result<Boolean> getExistColumn(String table, String column) {
        return Result.success("", tenantService.getExistColumn(table, column));
    }
}

package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.entity.BaseEntity;
import com.superb.common.database.vo.BatchId;
import com.superb.common.utils.PasswordUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.dto.Options;
import com.superb.system.api.entity.*;
import com.superb.system.api.vo.Distribution;
import com.superb.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-19 08:41
 */
@Valid
@Controller
@SuperbDataScope
@Mapping("tenantConfig")
@SaCheckPermission("system:tenant:config")
@Api(value = "租户系统配置管理", consumes = "权限：system:tenant:config")
public class TenantConfigController {

    @Inject
    private SystemPermissionService permissionService;
    @Inject
    private SystemTenantService tenantService;
    @Inject
    private SystemRoleService roleService;
    @Inject
    private SystemUserService userService;
    @Inject
    private UserDataScopeService userDataScopeService;
    @Inject
    private SystemOrganizationService organizationService;

    @Mapping(value = "menu/{tenantId}", method = MethodType.GET)
    @ApiOperation(value = "菜单列表")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true)
    public Result<List<SystemPermission>> menuQuery(@Path String tenantId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(SystemPermission::getTenantId, tenantId);
        // 查询第一级菜单
        queryWrapper.eq(SystemPermission::getMenuType, 0);
        List<SystemPermission> list = permissionService.list(queryWrapper);
        permissionService.buildTree(list);
        return Result.success(list);
    }

    @Mapping(value = "menu/delete/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id彻底删除")
    public Result<Boolean> delete(@Path String id) {
        if (permissionService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "menu/synchronization/{tenantId}", method = MethodType.POST)
    @ApiOperation(value = "根据id集合同步菜单", notes = "同步会将子菜单和权限同步")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true)
    public Result<Boolean> synchronization(@Body @Validated BatchId batchId, @Path String tenantId) {
        if (permissionService.synchronization(batchId, tenantId)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "tenantOptions", method = MethodType.GET)
    @ApiOperation(value = "租户下拉列表")
    public Result<List<Options>> tenantOptions() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("tenant_key AS `value`", "name AS label");
        List<Options> list = tenantService.listAs(queryWrapper, Options.class);
        return Result.success(list);
    }

    @Mapping(value = "menuOptions/{tenantId}", method = MethodType.GET)
    @ApiOperation(value = "租户一级菜单列表")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true)
    public Result<List<Options>> menuOptions(@Path String tenantId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("id AS `value`", "name AS label");
        queryWrapper.eq(SystemPermission::getMenuType, 0).eq(SystemPermission::getTenantId, tenantId);
        List<Options> list = permissionService.listAs(queryWrapper, Options.class);
        return Result.success(list);
    }


    @Mapping(value = "role/{tenantId}", method = MethodType.GET)
    @ApiOperation(value = "角色列表")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true)
    public Result<List<SystemRole>> roleQuery(@Path String tenantId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(SystemRole::getTenantId, tenantId);
        queryWrapper.eq(SystemRole::getType, 0);
        List<SystemRole> list = roleService.list(queryWrapper);
        return Result.success(list);
    }

    @Mapping(value = "roleAction", method = MethodType.POST)
    @ApiOperation(value = "角色操作")
    public Result<Boolean> roleAction(@Body SystemRole role) {
        if (StringUtils.isNotBlank(role.getId())) {
            // 删除权限
            permissionService.deletePermsByRoleId(role.getId());
        }
        if (roleService.saveOrUpdate(role)) {
            // 添加角色权限
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.select(SystemPermission::getId, SystemPermission::getMenuType);
            queryWrapper.eq(SystemPermission::getTenantId, role.getTenantId());
            List<SystemPermission> list = permissionService.list(queryWrapper);
            List<String> permIds = list.stream().filter(i -> i.getMenuType() == 2).map(SystemPermission::getId).toList();
            List<String> menus = list.stream().filter(i -> i.getMenuType() == 0 || i.getMenuType() == 1).map(SystemPermission::getId).toList();
            Distribution distribution = new Distribution();
            distribution.setRoleId(role.getId());
            distribution.setMenuIds(menus);
            distribution.setPermissionIds(permIds);
            permissionService.distribution(distribution);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "user/{tenantId}", method = MethodType.GET)
    @ApiOperation(value = "租户管理员列表")
    public Result<List<SystemUser>> userQuery(@Path String tenantId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(SystemUser::getSuperb, 1);
        queryWrapper.eq(SystemUser::getTenantId, tenantId);
        return Result.success(userService.list(queryWrapper));
    }

    @Mapping(value = "userAction", method = MethodType.POST)
    @ApiOperation(value = "租户管理员操作", notes = "新增/修改租户管理员，会新增默认数据权限，管理员数据权限默认为本部门及子部门")
    public Result<Boolean> userAction(@Body SystemUser user) {
        // 获取租户部门id
        Organization organization = organizationService.getOne(QueryWrapper.create()
                .eq(BaseEntity::getTenantId, user.getTenantId()).eq(Organization::getParentCode, 1).limit(1));
        if (StringUtils.isNotBlank(user.getId())) {
            // 删除管理员数据权限
            userDataScopeService.remove(QueryWrapper.create().eq(UserDataScope::getUserId, user.getId()).eq(UserDataScope::getTenantId, user.getTenantId()));
        } else {
            user.setOrganId(organization.getId());
            user.setSuperb(1);
        }
        String salt = RandomUtil.randomString(PasswordUtils.BASE_SALT, 16);
        user.setPassword(PasswordUtils.encrypt(salt, user.getPassword()));
        user.setSalt(salt);
        if (userService.saveOrUpdate(user)) {
            UserDataScope userDataScope = new UserDataScope();
            userDataScope.setTenantId(user.getTenantId());
            userDataScope.setUserId(user.getId());
            userDataScope.setOrganId(organization.getId());
            userDataScope.setDataScopeType(3);
            userDataScope.setIsMain(1);
            userDataScope.setEnable(0);
            // 获得租户系统角色id
            List<String> roleIds = roleService.listAs(QueryWrapper.create()
                    .eq(SystemRole::getTenantId, user.getTenantId()).select("id").eq(SystemRole::getType, 0), String.class);
            userDataScope.setRoleId(String.join(",", roleIds));
            // 保存数据权限
            userDataScopeService.save(userDataScope);
            return Result.success();
        }
        return Result.error();
    }
}

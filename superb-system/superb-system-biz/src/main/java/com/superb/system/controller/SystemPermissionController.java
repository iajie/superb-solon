package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.annotation.SuperbIgnoreTenant;
import com.superb.common.database.vo.BatchId;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.system.api.dto.MenuItem;
import com.superb.system.api.dto.Options;
import com.superb.system.api.dto.RolePerms;
import com.superb.system.api.dto.TopAndSideMenu;
import com.superb.system.api.entity.SystemPermission;
import com.superb.system.api.entity.SystemUser;
import com.superb.system.api.enums.MenuType;
import com.superb.system.api.vo.Distribution;
import com.superb.system.api.vo.PermissionAction;
import com.superb.system.service.SystemPermissionService;
import com.superb.system.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单权限表;(system_permission)表控制层
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Valid
@Controller
@SuperbDataScope
@SuperbIgnoreTenant
@Api("系统菜单权限表接口")
@Mapping("systemPermission")
public class SystemPermissionController {

    @Inject
    private SystemPermissionService permissionService;
    @Inject
    private SystemUserService userService;

    @Mapping(value = "query/{type}", method = MethodType.GET)
    @SaCheckPermission("system:menu:select")
    @ApiOperation(value = "菜单查询", notes = "权限: system:menu:select<br>")
    @ApiImplicitParam(name = "type", value = "菜单类型：0侧边栏菜单；1顶部菜单；2中台菜单", required = true)
    public Result<List<SystemPermission>> query(@Path String type) {
        List<SystemPermission> result = permissionService.menuTree(type, false);
        return Result.success(result);
    }

    @Mapping(value = "parentMenu/{type}", method = MethodType.GET)
    @SaCheckPermission("system:menu:select")
    @ApiOperation(value = "父级菜单", notes = "权限: system:menu:select<br>")
    public Result<List<Options>> parentMenu(@Path String type) {
        List<Options> result = permissionService.parentMenu(type);
        return Result.success(result);
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @SaCheckPermission("system:menu:insert")
    @ApiOperation(value = "新增信息", notes = "权限: system:menu:insert<br>")
    public Result<Boolean> insert(@Body @Validated SystemPermission systemPermission) {
        if (permissionService.save(systemPermission)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @SaCheckPermission("system:menu:update")
    @ApiOperation(value = "修改信息", notes = "权限: system:menu:update<br>")
    public Result<Boolean> update(@Body @Validated SystemPermission systemPermission) {
        if (permissionService.updateById(systemPermission)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "info/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id获取信息")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<SystemPermission> getInfo(@Path String id) {
        return Result.success(permissionService.getById(id));
    }

    @Mapping(value = "remove/{id}", method = MethodType.GET)
    @SaCheckPermission("system:menu:update")
    @ApiOperation(value = "根据id删除", notes = "权限: system:menu:update<br><font style='color: #1890FF'>假删除，会进入回收站</font>")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<Boolean> removeById(@Path String id) {
        if (permissionService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delQuery", method = MethodType.GET)
    @SaCheckPermission({"system:menu:select", "system:menu:delete"})
    @ApiOperation(value = "回收站", notes = "权限: <br>同时拥有system:menu:select、system:menu:delete")
    public Result<List<SystemPermission>> delQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("del", 1);
        queryWrapper.eq(SystemPermission::getTenantId, HeadersUtils.getTenantId());
        List<SystemPermission> result = LogicDeleteManager.execWithoutLogicDelete(() -> permissionService.list(queryWrapper));
        return Result.success(result);
    }

    @Mapping(value = "recovery", method = MethodType.POST)
    @SaCheckPermission({"system:menu:update", "system:menu:delete"})
    @ApiOperation(value = "根据id恢复删除的数据", notes = "权限: <br>同时拥有system:menu:update、system:menu:delete")
    public Result<Boolean> recovery(@Body @Validated BatchId batchId) {
        List<SystemPermission> list = new ArrayList<>();
        batchId.getId().forEach(i -> list.add(new SystemPermission(i, 0)));
        if (LogicDeleteManager.execWithoutLogicDelete(() -> permissionService.updateBatch(list))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delete", method = MethodType.POST)
    @SaCheckPermission("system:menu:delete")
    @ApiOperation(value = "根据id彻底删除", notes = "权限: system:menu:delete<br><font style='color: red'>真删除，数据无法恢复</font>")
    public Result<Boolean> delete(@Body @Validated BatchId batchId) {
        if (LogicDeleteManager.execWithoutLogicDelete(() -> permissionService.removeByIds(batchId.getId()))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "menu/{type}", method = MethodType.GET)
    @ApiOperation(value = "获取当前登录人的顶部菜单")
    @ApiImplicitParam(name = "type", value = "菜单类型：0侧边栏菜单；1顶部菜单；2中台菜单", required = true)
    public Result<List<MenuItem>> menu(@Path MenuType type) {
        SystemUser user = userService.getInfoById(StpUtil.getLoginId().toString());
        RedisKey key = new RedisKey(KeyType.PER, "menuCache:" + user.getId());
        List<MenuItem> menuItems = RedisUtils.build().hash().getNullSet(key, type.name(),
                () -> permissionService.menuList(user.getId(), type),
                redis -> redis.hash().get(key, type.name())
        );
        return Result.success(menuItems);
    }

    @Mapping(value = "topAndSideMenu", method = MethodType.GET)
    @ApiOperation(value = "获取当前登录人的顶部菜单和侧边栏菜单")
    public Result<TopAndSideMenu> topAndSideMenu() {
        SystemUser user = userService.getInfoById(StpUtil.getLoginId().toString());
        RedisKey key = new RedisKey(KeyType.TIME, Duration.ofMinutes(60), "menuCache:" + user.getId());
        TopAndSideMenu menuItems = RedisUtils.build().value().getNullSet(key, () -> {
            TopAndSideMenu menu = new TopAndSideMenu();
            if (user.getSuperb() == 1) {
                menu.setTop(permissionService.menuList(null, MenuType.top));
                menu.setSide(permissionService.menuList(null, MenuType.side));
            } else {
                menu.setTop(permissionService.menuList(user.getId(), MenuType.top));
                menu.setSide(permissionService.menuList(user.getId(), MenuType.side));
            }
            return menu;
        }, redis -> redis.value().get(key));
        return Result.success(menuItems);
    }

    @Mapping(value = "permissionList/{menuId}", method = MethodType.GET)
    @ApiOperation(value = "菜单权限")
    @ApiImplicitParam(name = "menuId", value = "菜单id", required = true)
    public Result<List<SystemPermission>> permissionList(@Path String menuId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(SystemPermission::getParentId, menuId).eq(SystemPermission::getMenuType, 2);
        queryWrapper.eq(SystemPermission::getTenantId, HeadersUtils.getTenantId());
        queryWrapper.select(SystemPermission::getId, SystemPermission::getRemarks, SystemPermission::getSort, SystemPermission::getName, SystemPermission::getPerms);
        queryWrapper.orderBy(SystemPermission::getSort).asc();
        List<SystemPermission> list = permissionService.list(queryWrapper);
        return Result.success(list);
    }

    @Mapping(value = "permissionAction", method = MethodType.POST)
    @SaCheckPermission("system:permission:manage")
    @ApiOperation(value = "菜单权限操作", notes = "权限：system:permission:manage")
    public Result<Boolean> permissionInsert(@Body @Validated PermissionAction action) {
        SystemPermission permission = new SystemPermission();
        BeanUtil.copyProperties(action, permission);
        permission.setMenuType(2);
        if (permissionService.saveOrUpdate(permission)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "rolePermission/{roleId}", method = MethodType.GET)
    @SaCheckPermission("system:permission:manage")
    @ApiOperation(value = "获取角色权限", notes = "权限：system:permission:manage")
    public Result<RolePerms> rolePermission(@Path String roleId) {
        return Result.success(permissionService.getRolePermission(roleId));
    }

    @Mapping(value = "permission/distribution", method = MethodType.POST)
    @SaCheckPermission("system:permission:distribution")
    @ApiOperation(value = "权限分配", notes = "权限：system:permission:distribution")
    public Result<Boolean> distribution(@Body @Validated Distribution distribution) {
        // 1. 先删除角色权限
        if (permissionService.deletePermsByRoleId(distribution.getRoleId())) {
            // 2. 将新权限添加
            permissionService.distribution(distribution);
            return Result.success("权限分配成功！");
        }
        return Result.error("权限分配操作失败！");
    }

    @Mapping(value = "permission/addDistribution", method = MethodType.POST)
    @SaCheckPermission("system:permission:distribution")
    @ApiOperation(value = "勾选权限时分配", notes = "权限：system:permission:distribution")
    public Result<Boolean> addDistribution(@Body @Validated Distribution distribution) {
        if (permissionService.distribution(distribution)) {
            return Result.success("权限分配成功！");
        }
        return Result.error("权限分配操作失败！");
    }

    @Mapping(value = "permission/removeDistribution", method = MethodType.POST)
    @SaCheckPermission("system:permission:distribution")
    @ApiOperation(value = "取消勾选时删除权限", notes = "权限：system:permission:distribution")
    public Result<Boolean> removeDistribution(@Body @Validated Distribution distribution) {
        // 1. 先删除角色权限
        if (distribution.getMenuIds().size() > 0) {
            permissionService.deletePermsByRoleId(distribution.getRoleId(), distribution.getMenuIds().get(0));
        }
        if (distribution.getPermissionIds().size() > 0) {
            permissionService.deletePermsByRoleId(distribution.getRoleId(), distribution.getPermissionIds().get(0));
        }
        return Result.success("取消权限成功！");
    }

}
package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.annotation.SuperbIgnoreTenant;
import com.superb.common.database.vo.BatchId;
import com.superb.common.database.vo.PageParams;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.entity.SystemPermission;
import com.superb.system.api.entity.SystemRole;
import com.superb.system.service.SystemPermissionService;
import com.superb.system.service.SystemRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统角色;(system_role)表控制层
 * @Author: ajie
 * @CreateTime: 2024-6-3
 */
@Valid
@Controller
@SuperbDataScope
@SuperbIgnoreTenant
@Mapping("/systemRole")
@Api("系统角色接口")
public class SystemRoleController {

    @Inject
    private SystemRoleService systemRoleService;
    @Inject
    private SystemPermissionService permissionService;

    @Mapping(value = "pageQuery", method = MethodType.POST)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "分页查询", notes = "权限: system:role:manage<br>")
    public Result<Page<SystemRole>> pageQuery(@Body PageParams<SystemRole> pageParams) {
        SystemRole params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(SystemRole::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(SystemRole::getCode, params.getCode(), StringUtils.isNotBlank(params.getCode()));
        queryWrapper.eq(SystemRole::getTenantId, HeadersUtils.getTenantId());
        if (StringUtils.isNotBlank(params.getOrganId())) {
            queryWrapper.eq(SystemRole::getOrganId, params.getOrganId());
        } else {
            queryWrapper.and(l -> {
                l.eq(SystemRole::getOrganId, "").or(i -> {
                    i.isNull(SystemRole::getOrganId);
                });
            });
        }
        queryWrapper.orderBy(SystemRole::getSort).asc();
        Page<SystemRole> result = systemRoleService.page(pageParams.getPage(), queryWrapper);
        return Result.success(result);
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "新增信息", notes = "权限: system:role:manage<br>")
    public Result<Boolean> insert(@Body @Validated SystemRole systemRole) {
        // 系统角色则不存在部门
        if (systemRole.getType() == 0) {
            systemRole.setOrganId("");
        }
        if (systemRoleService.save(systemRole)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "修改信息", notes = "权限: system:role:manage<br>")
    public Result<Boolean> update(@Body @Validated SystemRole systemRole) {
        if (systemRoleService.updateById(systemRole)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "info/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id获取信息")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<SystemRole> getInfo(@Path String id) {
        return Result.success(systemRoleService.getById(id));
    }

    @Mapping(value = "remove/{id}", method = MethodType.GET)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "根据id删除", notes = "权限: system:role:manage<br><font style='color: #1890FF'>假删除，会进入回收站</font>")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<Boolean> removeById(@Path String id) {
        if (systemRoleService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delQuery", method = MethodType.GET)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "回收站", notes = "权限: <br>system:role:manage")
    public Result<List<SystemRole>> delQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("del", 1);
        queryWrapper.eq(SystemRole::getTenantId, HeadersUtils.getTenantId());
        List<SystemRole> result = LogicDeleteManager.execWithoutLogicDelete(() -> systemRoleService.list(queryWrapper));
        return Result.success(result);
    }

    @Mapping(value = "recovery", method = MethodType.POST)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "根据id恢复删除的数据", notes = "权限: <br>system:role:manage")
    public Result<Boolean> recovery(@Body @Validated BatchId batchId) {
        List<SystemRole> list = new ArrayList<>();
        batchId.getId().forEach(i -> list.add(new SystemRole(i, 0)));
        if (LogicDeleteManager.execWithoutLogicDelete(() -> systemRoleService.updateBatch(list))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delete", method = MethodType.POST)
    @SaCheckPermission(value = {"system:role:manage"}, mode = SaMode.OR)
    @ApiOperation(value = "根据id彻底删除", notes = "权限: system:role:manage<br><font style='color: red'>真删除，数据无法恢复</font>")
    public Result<Boolean> delete(@Body @Validated BatchId batchId) {
        if (LogicDeleteManager.execWithoutLogicDelete(() -> systemRoleService.removeByIds(batchId.getId()))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "menuPermission/{type}", method = MethodType.GET)
    @SaCheckPermission("system:menu:select")
    @ApiOperation(value = "菜单权限查询", notes = "权限: system:menu:select<br>")
    @ApiImplicitParam(name = "type", value = "菜单类型：0侧边栏菜单；1顶部菜单；2中台菜单", required = true)
    public Result<List<SystemPermission>> query(@Path String type) {
        List<SystemPermission> result = permissionService.menuTree(type, true);
        return Result.success(result);
    }

    @Mapping(value = "systemList", method = MethodType.GET)
    @ApiOperation(value = "系统角色列表项")
    public Result<List<SystemRole>> systemList() {
        QueryWrapper queryWrapper = new QueryWrapper();
        // 系统角色
        queryWrapper.or(l -> {
            l.eq(SystemRole::getOrganId, "").isNull(SystemRole::getOrganId);
        });
        queryWrapper.eq(SystemRole::getTenantId, HeadersUtils.getTenantId());
        queryWrapper.orderBy(SystemRole::getSort).asc();
        List<SystemRole> result = systemRoleService.list(queryWrapper);
        return Result.success(result);
    }
}
package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.entity.BaseEntity;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.dto.Options;
import com.superb.system.api.entity.SystemRole;
import com.superb.system.api.entity.UserDataScope;
import com.superb.system.api.vo.UpdateScopeMain;
import com.superb.system.service.SystemOrganizationService;
import com.superb.system.service.SystemRoleService;
import com.superb.system.service.UserDataScopeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户数据权限范围
 * @Author: ajie
 * @CreateTime: 2024-7-3
 */
@Valid
@Controller
@SuperbDataScope
@Mapping("/userDataScope")
@Api("用户数据权限范围")
@SaCheckPermission("system:dataScope:manage")
public class UserDataScopeController {

    @Inject
    private UserDataScopeService dataScopeService;
    @Inject
    private SystemRoleService roleService;
    @Inject
    private SystemOrganizationService organizationService;

    @Mapping(value = "list/{userId}", method = MethodType.GET)
    @ApiOperation(value = "用户数据权限查询", notes = "权限: system:dataScope:manage<br>")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true)
    public Result<List<UserDataScope>> listQuery(@Path String userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(UserDataScope::getUserId, userId);
        // 默认部门在前
        queryWrapper.orderBy(UserDataScope::getIsMain).desc();
        List<UserDataScope> result = dataScopeService.list(queryWrapper);
        for (UserDataScope dataScope : result) {
            String roleId = dataScope.getRoleId();
            if (StringUtils.isNotBlank(roleId)) {
                String[] roleIds = roleId.split(",");
                QueryWrapper rlqw = new QueryWrapper();
                rlqw.select("id AS `value`", "name AS label");
                rlqw.in(SystemRole::getId, Arrays.asList(roleId.split(",")));
                List<Options> roles = roleService.listAs(rlqw, Options.class);
                dataScope.setRoleIds(List.of(roleIds));
                dataScope.setRoles(roles);
            }
            String organId = dataScope.getDataScopeOrganId();
            // 数据权限自定义且部门不为空
            if (StringUtils.isNotBlank(organId) && dataScope.getDataScopeType() == 4) {
                QueryWrapper rlqw = new QueryWrapper();
                rlqw.select("id AS `value`", "name AS label");
                rlqw.in(BaseEntity::getId, Arrays.asList(organId.split(",")));
                List<Options> list = organizationService.listAs(rlqw, Options.class);
                dataScope.setOrgans(list);
            }
        }
        return Result.success(result);
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @ApiOperation(value = "新增用户数据权限", notes = "权限: system:dataScope:manage<br>")
    public Result<Boolean> insert(@Body @Validated UserDataScope dataScope) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(UserDataScope::getUserId, dataScope.getUserId());
        queryWrapper.eq(UserDataScope::getOrganId, dataScope.getOrganId());
        if (dataScopeService.count(queryWrapper) > 0) {
            return Result.success("当前部门已存在数据权限，无法再次创建！");
        }
        if (dataScope.getDataScopeType() == 4) {
            List<Options> organs = dataScope.getOrgans();
            if (organs.size() < 1) {
                return Result.error("自定义数据权限部门不能为空！");
            }
            // 逗号分割的部门id
            dataScope.setDataScopeOrganId(organs.stream().map(Options::getValue).collect(Collectors.joining(",")));
        }
        if (!dataScope.getRoleIds().isEmpty() && dataScope.getRoleIds().size() > 0) {
            // 逗号分割角色id
            dataScope.setRoleId(String.join(",", dataScope.getRoleIds()));
        }
        if (dataScopeService.save(dataScope)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @ApiOperation(value = "修改用户数据权限", notes = "权限: system:dataScope:manage<br>")
    public Result<Boolean> update(@Body @Validated UserDataScope dataScope) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(UserDataScope::getUserId, dataScope.getUserId());
        queryWrapper.eq(UserDataScope::getOrganId, dataScope.getOrganId());
        // 不算当前修改部门
        queryWrapper.ne(UserDataScope::getId, dataScope.getId());
        if (dataScopeService.count(queryWrapper) > 0) {
            return Result.success("当前部门已存在数据权限，修改时无法选中！");
        }
        if (dataScope.getDataScopeType() == 4) {
            List<Options> organs = dataScope.getOrgans();
            if (organs.size() < 1) {
                return Result.error("自定义数据权限部门不能为空！");
            }
            // 逗号分割的部门id
            dataScope.setDataScopeOrganId(organs.stream().map(Options::getValue).collect(Collectors.joining(",")));
        } else {
            dataScope.setDataScopeOrganId("");
        }
        if (!dataScope.getRoleIds().isEmpty() && dataScope.getRoleIds().size() > 0) {
            // 逗号分割角色id
            dataScope.setRoleId(String.join(",", dataScope.getRoleIds()));
        } else {
            dataScope.setRoleId("");
        }
        if (dataScopeService.updateById(dataScope)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "updateMain", method = MethodType.POST)
    @ApiOperation(value = "用户数据权限是否默认", notes = "权限: system:dataScope:manage<br>")
    public Result<Boolean> updateMain(@Body @Validated UpdateScopeMain scopeMain) {
        if (scopeMain.getIsMain() == 1) {
            // 1.获取数据权限信息
            UserDataScope dataScope = dataScopeService.getById(scopeMain.getId());
            // 2.将该数据权限的用户其他数据权限设置为否
            dataScopeService.updateChain()
                    .set(UserDataScope::getIsMain, 0)
                    .eq(UserDataScope::getUserId, dataScope.getUserId())
                    .update();
            // 3.将传递的id权限设置为默认
            UserDataScope entity = new UserDataScope();
            entity.setId(scopeMain.getId());
            entity.setIsMain(scopeMain.getIsMain());
            dataScopeService.updateById(entity);
        } else {
            dataScopeService.updateChain().eq(UserDataScope::getId, scopeMain.getId()).set(UserDataScope::getIsMain, 0).update();
        }
        return Result.success();
    }
}
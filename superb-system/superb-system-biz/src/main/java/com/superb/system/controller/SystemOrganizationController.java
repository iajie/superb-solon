package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.enums.DataScope;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.vo.BatchId;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.security.entity.SuperbUserDataScope;
import com.superb.common.security.utils.SuperbUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.entity.Organization;
import com.superb.system.service.SystemOrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 部门机构;(system_organization)表控制层
 * @Author: ajie
 * @CreateTime: 2024-6-14
 */
@Valid
@Controller
@SuperbDataScope
@Api("部门机构接口")
@Mapping("/organization")
public class SystemOrganizationController {

    @Inject
    private SystemOrganizationService organizationService;

    @Mapping(value = "tree", method = MethodType.GET)
    @ApiOperation(value = "当前用户部门树")
    public Result<List<Organization>> tree() {
        SuperbUserDataScope dataScope = SuperbUtils.getUserDataScope();
        DataScope scope = DataScope.ofValue(dataScope.getDataScopeType());
        String organizationId = HeadersUtils.getOrganizationId();
        // 获取当前部门
        List<Organization> organizations = organizationService.listByIds(Collections.singletonList(organizationId));
        if (organizations.isEmpty()) {
            return Result.error("当前部门不存在！");
        }
        switch (scope) {
            case ORGAN_AND_SUB, ORGAN_SUB -> {
                // 构建子级
                organizationService.buildTree(organizations.get(0));
                // 如果数据权限为子部门
                if (scope == DataScope.ORGAN_SUB) {
                    // 只拿子部门
                    organizations = organizations.get(0).getChildren();
                }
            }
            case CUSTOM -> {
                // 获取自定义部门
                String organId = dataScope.getDataScopeOrganId();
                if (StringUtils.isNotBlank(organId)) {
                    // 自定义部门
                    List<Organization> customOrganizations = organizationService.listByIds(Arrays.asList(organId.split(",")));
                    // 如果自定义部门数据权限为本部门及子级部门
                    if (dataScope.getDataScopeOrganType() == 1) {
                        for (Organization customOrganization : customOrganizations) {
                            // 构建子级
                            organizationService.buildTree(customOrganization);
                        }
                    }
                    // 自定义部门
                    organizations = customOrganizations;
                }
            }
            case ALL -> {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq(Organization::getParentCode, 0);
                List<Organization> list = organizationService.list(queryWrapper);
                for (Organization organization : list) {
                    // 构建子级
                    organizationService.buildTree(organization);
                }
                organizations = list;
            }
        }
        return Result.success(organizations);
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @SaCheckPermission("system:organization:insert")
    @ApiOperation(value = "新增信息", notes = "权限: system:organization:insert<br>")
    public Result<Boolean> insert(@Body @Validated Organization organization) {
        if (organizationService.save(organization)) {
            RedisKey key = new RedisKey(KeyType.TIME,"organization*");
            RedisUtils.build().dels(key);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @SaCheckPermission("system:organization:update")
    @ApiOperation(value = "修改信息", notes = "权限: system:organization:update<br>")
    public Result<Boolean> update(@Body @Validated Organization organization) {
        if (organizationService.updateById(organization)) {
            RedisKey key = new RedisKey(KeyType.TIME,"organization*");
            RedisUtils.build().dels(key);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "info/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id获取信息")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<Organization> getInfo(@Path String id) {
        return Result.success(organizationService.getById(id));
    }

    @Mapping(value = "remove/{id}", method = MethodType.GET)
    @SaCheckPermission("system:organization:update")
    @ApiOperation(value = "根据id删除", notes = "权限: system:organization:update<br><font style='color: #1890FF'>假删除，会进入回收站</font>")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<Boolean> removeById(@Path String id) {
        if (organizationService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delQuery", method = MethodType.GET)
    @SaCheckPermission({"system:organization:select", "system:organization:delete"})
    @ApiOperation(value = "回收站", notes = "权限: <br>同时拥有system:organization:select、system:organization:delete")
    public Result<List<Organization>> delQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("del", 1);
        List<Organization> result = LogicDeleteManager.execWithoutLogicDelete(() -> organizationService.list(queryWrapper));
        return Result.success(result);
    }

    @Mapping(value = "recovery", method = MethodType.POST)
    @SaCheckPermission({"system:organization:update", "system:organization:delete"})
    @ApiOperation(value = "根据id恢复删除的数据", notes = "权限: <br>同时拥有system:organization:update、system:organization:delete")
    public Result<Boolean> recovery(@Body @Validated BatchId batchId) {
        List<Organization> list = new ArrayList<>();
        batchId.getId().forEach(i -> list.add(new Organization(i, 0)));
        if (LogicDeleteManager.execWithoutLogicDelete(() -> organizationService.updateBatch(list))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delete", method = MethodType.POST)
    @SaCheckPermission("system:organization:delete")
    @ApiOperation(value = "根据id彻底删除", notes = "权限: system:organization:delete<br><font style='color: red'>真删除，数据无法恢复</font>")
    public Result<Boolean> delete(@Body @Validated BatchId batchId) {
        if (LogicDeleteManager.execWithoutLogicDelete(() -> organizationService.removeByIds(batchId.getId()))) {
            return Result.success();
        }
        return Result.error();
    }
}
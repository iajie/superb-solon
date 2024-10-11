package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.mask.MaskManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.vo.BatchId;
import com.superb.common.database.vo.PageParams;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.entity.Organization;
import com.superb.system.api.entity.SystemTenant;
import com.superb.system.service.SystemOrganizationService;
import com.superb.system.service.SystemTenantService;
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
 * 系统租户管理表;(system_tenant)表控制层
 * @Author: ajie
 * @CreateTime: 2024-5-11
 */
@Valid
@Controller
@SuperbDataScope
@Mapping("/systemTenant")
@Api("系统租户管理表接口")
public class SystemTenantController {

    @Inject
    private SystemTenantService tenantService;
    @Inject
    private SystemOrganizationService organizationService;

    @Mapping(value = "pageQuery", method = MethodType.POST)
    @SaCheckPermission("system:tenant:select")
    @ApiOperation(value = "分页查询", notes = "权限: system:tenant:select<br>")
    public Result<Page<SystemTenant>> pageQuery(@Body PageParams<SystemTenant> pageParams) {
        SystemTenant params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(SystemTenant::getTenantKey, params.getTenantKey(), StringUtils.isNotBlank(params.getTenantKey()));
        queryWrapper.like(SystemTenant::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(SystemTenant::getLegalPerson, params.getLegalPerson(), StringUtils.isNotBlank(params.getLegalPerson()));
        queryWrapper.like(SystemTenant::getPhone, params.getPhone(), StringUtils.isNotBlank(params.getPhone()));
        Page<SystemTenant> result = tenantService.page(pageParams.getPage(), queryWrapper);
        return Result.success(result);
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @SaCheckPermission("system:tenant:insert")
    @ApiOperation(value = "新增信息", notes = "权限:system:tenant:insert<br>")
    public Result<Boolean> insert(@Body @Validated SystemTenant systemTenant) {
        if (tenantService.save(systemTenant)) {
            // 新增对应部门
            Organization organization = new Organization();
            organization.setTenantId(systemTenant.getTenantKey());
            organization.setName(systemTenant.getName());
            organization.setAddress(systemTenant.getRegisteredAddress());
            organization.setType(1);
            organization.setSort(10);
            organization.setParentCode(1);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(Organization::getParentCode, 0);
            Organization superb = organizationService.getOne(queryWrapper);
            // 父级
            organization.setParentId(superb.getId());
            // 新增租户部门
            organizationService.save(organization);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @SaCheckPermission("system:tenant:update")
    @ApiOperation(value = "修改信息", notes = "权限: system:tenant:update<br>")
    public Result<Boolean> update(@Body @Validated SystemTenant systemTenant) {
        if (tenantService.updateById(systemTenant)) {
            RedisKey key1 = new RedisKey(KeyType.PER, "tenantInfo");
            RedisKey key2 = new RedisKey(KeyType.PER, "info");
            RedisUtils.build().del(key1, key2);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "remove/{id}", method = MethodType.GET)
    @SaCheckPermission("system:tenant:update")
    @ApiOperation(value = "根据id删除", notes = "权限: system:tenant:update<br><font style='color: #1890FF'>假删除，会进入回收站</font>")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<Boolean> removeById(@Path String id) {
        if (tenantService.removeById(id)) {
            RedisKey key1 = new RedisKey(KeyType.PER, "tenantInfo");
            RedisKey key2 = new RedisKey(KeyType.PER, "info");
            RedisUtils.build().del(key1, key2);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "info/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id获取信息")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<SystemTenant> getInfo(@Path String id) {
        SystemTenant tenant = MaskManager.execWithoutMask(() -> tenantService.getById(id));
        return Result.success(tenant);
    }

    @Mapping(value = "delQuery", method = MethodType.GET)
    @SaCheckPermission({"system:tenant:select", "system:tenant:delete"})
    @ApiOperation(value = "回收站", notes = "权限: <br>同时拥有system:tenant:select、system:tenant:delete")
    public Result<List<SystemTenant>> delQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("del", 1);
        List<SystemTenant> result = LogicDeleteManager.execWithoutLogicDelete(() -> tenantService.list(queryWrapper));
        return Result.success(result);
    }

    @Mapping(value = "recovery", method = MethodType.POST)
    @SaCheckPermission({"system:tenant:update", "system:tenant:delete"})
    @ApiOperation(value = "根据id恢复删除的数据", notes = "权限: <br>同时拥有system:tenant:update、system:tenant:delete")
    public Result<Boolean> recovery(@Body @Validated BatchId batchId) {
        List<SystemTenant> list = new ArrayList<>();
        batchId.getId().forEach(i -> list.add(new SystemTenant(i, 0)));
        if (LogicDeleteManager.execWithoutLogicDelete(() -> tenantService.updateBatch(list))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delete", method = MethodType.POST)
    @SaCheckPermission("system:tenant:delete")
    @ApiOperation(value = "根据id彻底删除", notes = "权限: system:tenant:delete<br><font style='color: red'>真删除，数据无法恢复</font>")
    public Result<Boolean> delete(@Body @Validated BatchId batchId) {
        if (LogicDeleteManager.execWithoutLogicDelete(() -> tenantService.removeByIds(batchId.getId()))) {
            return Result.success();
        }
        return Result.error();
    }

}
package com.superb.allocation.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.allocation.api.entity.AllocationPosition;
import com.superb.allocation.service.AllocationPositionService;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.vo.PageParams;
import com.superb.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 行政区划;(allocation_position)表控制层
 * @Author: ajie
 * @CreateTime: 2024-7-1
 */
@Valid
@Controller
@SuperbDataScope
@Mapping("/allocationPosition")
@Api("行政区划接口")
public class AllocationPositionController {

    @Inject
    private AllocationPositionService positionService;

    @Mapping(value = "query", method = MethodType.POST)
    @ApiOperation(value = "级别查询", notes = "分页无效")
    public Result<List<AllocationPosition>> pageQuery(@Body PageParams<AllocationPosition> pageParams) {
        AllocationPosition params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(AllocationPosition::getLevel, params.getLevel(), StringUtils.isNotBlank(params.getLevel()));
        queryWrapper.eq(AllocationPosition::getParentId, params.getParentId(), StringUtils.isNotBlank(params.getParentId()));
        queryWrapper.eq(AllocationPosition::getId, params.getId(), StringUtils.isNotBlank(params.getId()));
        return Result.success(positionService.list(queryWrapper));
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @SaCheckPermission("allocation:position:insert")
    @ApiOperation(value = "新增信息", notes = "权限: allocation:position:insert<br>")
    public Result<Boolean> insert(@Body @Validated AllocationPosition allocationPosition) {
        if (positionService.save(allocationPosition)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @SaCheckPermission("allocation:position:update")
    @ApiOperation(value = "修改信息", notes = "权限: allocation:position:update<br>")
    public Result<Boolean> update(@Body @Validated AllocationPosition allocationPosition) {
        if (positionService.updateById(allocationPosition)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "remove/{id}", method = MethodType.GET)
    @SaCheckPermission("allocation:position:delete")
    @ApiOperation(value = "根据id删除", notes = "权限: allocation:position:delete<br><font style='color: #1890FF'>假删除，会进入回收站</font>")
    @ApiImplicitParam(name = "id", value = "业务主键", required = true)
    public Result<Boolean> removeById(@Path String id) {
        if (positionService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }
}
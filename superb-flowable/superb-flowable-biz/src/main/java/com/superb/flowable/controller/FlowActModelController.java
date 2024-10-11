package com.superb.flowable.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.vo.PageParams;
import com.superb.common.utils.StringUtils;
import com.superb.flowable.api.entity.ActReProcdef;
import com.superb.flowable.api.entity.FlowActModel;
import com.superb.flowable.service.FlowActModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.NoRepeatSubmit;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-12 09:12
 */
@Valid
@Controller
@Mapping("flowActModel")
@Api("流程模型管理")
@SuperbDataScope
public class FlowActModelController {

    @Inject
    private FlowActModelService actModelService;

    /**
     * @author: ajie
     * @date: 2024/8/12 10:18
     * @description: 流程模型查询
     */
    @ApiOperation(value = "流程模型分页查询")
    @SaCheckPermission("flowable:model:select")
    @Mapping(value = "page", method = MethodType.POST)
    public Result<Page<FlowActModel>> page(@Body PageParams<FlowActModel> pageParams) {
        FlowActModel params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(FlowActModel::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(FlowActModel::getKey, params.getKey(), StringUtils.isNotBlank(params.getKey()));
        queryWrapper.eq(FlowActModel::getModelType, params.getModelType(), StringUtils.isNotBlank(params.getModelType()));
        queryWrapper.orderBy(FlowActModel::getCreateTime);
        return Result.success(actModelService.page(pageParams.getPage(), queryWrapper));
    }

    /**
     * @author: ajie
     * @date: 2024/8/13 17:22
     * @description: 流程创建更新/防重复提交
     */
    @NoRepeatSubmit(seconds = 10)
    @SaCheckPermission("flowable:model:save")
    @ApiOperation(value = "流程模型保存", notes = "创建/更新流程模型")
    @Mapping(value = "save", method = MethodType.POST)
    public Result<Boolean> save(@Body @Validated FlowActModel flowActModel) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(FlowActModel::getKey, flowActModel.getKey());
        if (StringUtils.isNotBlank(flowActModel.getId())) {
            flowActModel.setLastUpdateTime(new Date());
            queryWrapper.ne(FlowActModel::getId, flowActModel.getId());
        } else {
            // 模型版本
            flowActModel.setRev(0);
        }
        if (actModelService.count(queryWrapper) > 0) {
            return Result.error("当前模型标识已存在，无法创建!");
        }
        return Result.success(actModelService.saveOrUpdate(flowActModel));
    }

    @SaCheckPermission("flowable:model:delete")
    @ApiOperation(value = "流程模型删除")
    @Mapping(value = "remove/{id}", method = MethodType.GET)
    public Result<Boolean> remove(@Path String id) {
        return Result.success(actModelService.removeById(id));
    }

    @ApiOperation(value = "流程模型获取")
    @Mapping(value = "info/{id}", method = MethodType.GET)
    public Result<FlowActModel> getInfo(@Path String id) {
        return Result.success(actModelService.getById(id));
    }

}

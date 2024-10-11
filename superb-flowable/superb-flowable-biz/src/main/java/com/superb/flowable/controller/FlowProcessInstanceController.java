package com.superb.flowable.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.dto.FlowProcessInstance;
import com.superb.flowable.api.dto.FlowUserTask;
import com.superb.flowable.api.dto.Option;
import com.superb.flowable.service.FlowProcessInstanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Path;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-22 10:14
 */
@Valid
@Controller
@SuperbDataScope
@Api("流程实例控制器")
@Mapping("processInstance")
@SaCheckPermission("flowable:process:list")
public class FlowProcessInstanceController {

    @Inject
    private FlowProcessInstanceService processInstanceService;

    @Mapping(value = "list/{flowKey}", method = MethodType.GET)
    @ApiOperation(value = "获取启动的流程实例", notes = "包含流程终止和激活的")
    public Result<List<FlowProcessInstance>> list(@Path String flowKey) {
        return Result.success(processInstanceService.getFlowInstanceList(flowKey));
    }

    @SaCheckPermission("flowable:process:state")
    @Mapping(value = "stateSet/{processInstanceId}", method = MethodType.GET)
    @ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = true)
    @ApiOperation(value = "流程实例状态设置", notes = "当实例状态为激活时会设置为终止，当实例状态为终止时状态会激活")
    public Result<Boolean> stateSet(@Path String processInstanceId) {
        return Result.success(processInstanceService.updateProcessInstanceState(processInstanceId));
    }

    @Mapping(value = "backUserTasks/{processInstanceId}", method = MethodType.GET)
    @ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = true)
    @ApiOperation(value = "根据流程实例Id获取可回退的节点")
    public Result<List<Option>> backUserTasks(@Path String processInstanceId) {
        return Result.success(processInstanceService.getFlowBackUserTasks(processInstanceId));
    }

    @Mapping(value = "backUserTasks/{processInstanceId}", method = MethodType.GET)
    @ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = true)
    @ApiOperation(value = "根据流程实例Id获取流程执行历史")
    public Result<List<FlowExecutionHistory>> executionHistory(@Path String processInstanceId) {
        return Result.success(processInstanceService.getFlowExecutionHistoryList(processInstanceId));
    }
}

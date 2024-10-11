package com.superb.flowable.controller;

import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.flowable.api.dto.FlowUserTask;
import com.superb.flowable.api.vo.FlowCancellation;
import com.superb.flowable.api.vo.FlowExecuteNextStep;
import com.superb.flowable.service.FlowDeploymentService;
import com.superb.flowable.service.FlowTaskService;
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
 * @CreateTime: 2024-08-21 13:48
 */
@Valid
@Controller
@Mapping("userTask")
@SuperbDataScope
@Api("流程任务接口")
public class FlowTaskController {

    @Inject
    private FlowTaskService taskService;

    @ApiOperation(value = "执行任务")
    @Mapping(value = "execute",method = MethodType.POST)
    public Result<List<FlowUserTask>> taskUserList(@Body @Validated FlowExecuteNextStep executeNextStep) {
        taskService.executeNextStep(executeNextStep);
        return Result.success();
    }

    @ApiOperation(value = "流程作废")
    @Mapping(value = "cancellation",method = MethodType.POST)
    public Result<List<FlowUserTask>> taskUserList(@Body @Validated FlowCancellation cancellation) {
        taskService.cancellationProcessInstance(cancellation);
        return Result.success();
    }

    @ApiOperation("获取当前任务节点执行人(候选人)")
    @Mapping(value = "executors/{taskId}",method = MethodType.GET)
    public Result<List<String>> getUserTaskExecutors(@Path String taskId) {
        return Result.success(taskService.getUserTaskExecutors(taskId, false));
    }

    @ApiOperation("获取当前任务节点执行部门(候选组)")
    @Mapping(value = "executeOrgan/{taskId}",method = MethodType.GET)
    public Result<List<String>> executeOrgan(@Path String taskId) {
        return Result.success(taskService.getUserTaskOrganIds(taskId));
    }
}

package com.superb.flowable.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.vo.PageParams;
import com.superb.common.utils.StringUtils;
import com.superb.flowable.api.dto.DeploymentProcdef;
import com.superb.flowable.api.dto.FlowUserTask;
import com.superb.flowable.api.entity.ActReProcdef;
import com.superb.flowable.api.entity.FlowReDeployment;
import com.superb.flowable.service.FlowDeploymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-20 16:15
 */
@Valid
@Controller
@SuperbDataScope
@Api("流程部署管理")
@Mapping("flowDeployment")
public class FlowDeploymentController {

    @Inject
    private FlowDeploymentService flowDeploymentService;


    /**
     * @author: ajie
     * @date: 2024/8/21 10:18
     * @description: 流程部署实例分页查询
     */
    @ApiOperation(value = "流程部署定义分页查询")
    @SaCheckPermission("flowable:deployment:select")
    @Mapping(value = "page", method = MethodType.POST)
    public Result<Page<DeploymentProcdef>> page(@Body PageParams<DeploymentProcdef> pageParams) {
        DeploymentProcdef params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("rd.ID_ AS id", "rp.ID_ AS processDefinitionId", "rd.NAME_ AS name", "rp.HAS_START_FORM_KEY_ AS hasStartFormKey",
                "rd.DEPLOY_TIME_ AS deploymentTime", "rd.KEY_ AS `key`", "rd.CATEGORY_ AS modelType", "rd.TENANT_ID_ AS tenantId",
                "rp.VERSION_ AS version", "rp.SUSPENSION_STATE_ AS suspensionState");
        // 主表
        queryWrapper.from(FlowReDeployment.class).as("rd");
        queryWrapper.like(FlowReDeployment::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(FlowReDeployment::getFlowKey, params.getKey(), StringUtils.isNotBlank(params.getKey()));
        queryWrapper.eq(FlowReDeployment::getModelType, params.getModelType(), StringUtils.isNotBlank(params.getModelType()));
        queryWrapper.leftJoin(ActReProcdef.class).as("rp").on(FlowReDeployment::getId, ActReProcdef::getDeploymentId);
        queryWrapper.orderBy(FlowReDeployment::getDeploymentTime);
        return Result.success(flowDeploymentService.pageAs(pageParams.getPage(), queryWrapper, DeploymentProcdef.class));
    }

    @Mapping(value = "deployment/{modelId}", method = MethodType.GET)
    @ApiOperation(value = "通过ModelId部署流程")
    @SaCheckPermission("flowable:deployment")
    public Result<String> deploymentModel(@Path String modelId) {
        return Result.success("流程部署成功！", flowDeploymentService.deploymentModel(modelId));
    }

    /**
     * @param deploymentId 实例id
     * @param cascade 是否级联删除
     * @Returns: {@link Result<String>}
     * @author mojie
     * @date: 2024/8/21 13:35
     * @description: 删除流程实例
     */
    @Mapping(value = "deleteDeployment", method = MethodType.GET)
    @ApiOperation(value = "删除流程定义")
    @SaCheckPermission("flowable:deployment:delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deploymentId", value = "部署id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "cascade", value = "是否级联删除", paramType = "query", dataTypeClass = Boolean.class),
    })
    public Result<String> deleteDeployment(@Param String deploymentId, @Param Boolean cascade) {
        if (cascade == null) {
            cascade = false;
        }
        flowDeploymentService.deleteDeployment(deploymentId, cascade);
        return Result.success("流程定义删除成功！");
    }

    @Mapping(value = "deploymentState/{processDefinitionId}", method = MethodType.GET)
    @ApiOperation(value = "流程定义状态设置", notes = "设置流程状态（当流程为：1激活（默认设置为终止流程）2：中止（挂起）（默认设置为激活流程））")
    @SaCheckPermission("flowable:deployment:state")
    @ApiImplicitParam(name = "processDefinitionId", value = "流程定义id", required = true, paramType = "path")
    public Result<String> deploymentState(@Path String processDefinitionId) {
        return Result.success("流程定义状态设置成功！", flowDeploymentService.deploymentState(processDefinitionId));
    }

    @Mapping(value = "flowUserList/{flowKey}", method = MethodType.GET)
    @ApiOperation(value = "流程定义用户任务节点", notes = "当前流程定义中存在用户任务的节点列表")
    @SaCheckPermission("flowable:deployment:select")
    @ApiImplicitParam(name = "flowKey", value = "流程定义key", required = true, paramType = "path")
    public Result<List<FlowUserTask>> getFlowUserTaskList(@Path String flowKey) {
        return Result.success(flowDeploymentService.getAllFlowUserTask(flowKey));
    }


    @Mapping(value = "loadPng/{processDefinitionId}", method = MethodType.GET)
    @ApiOperation(value = "流程定义用户任务节点", notes = "当前流程定义中存在用户任务的节点列表")
    @SaCheckPermission("flowable:deployment:select")
    @ApiImplicitParam(name = "processDefinitionId", value = "流程定义id", required = true, paramType = "path")
    public void loadPng(@Path String processDefinitionId) {
        flowDeploymentService.loadPng(processDefinitionId);
    }
}

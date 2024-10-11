package com.superb.flowable.service.impl;

import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import com.superb.flowable.api.dto.FlowUserTask;
import com.superb.flowable.api.entity.FlowActModel;
import com.superb.flowable.api.entity.FlowReDeployment;
import com.superb.flowable.mapper.FlowDeploymentMapper;
import com.superb.flowable.service.FlowActModelService;
import com.superb.flowable.service.FlowDeploymentService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.data.annotation.Tran;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ajie
 * @date: 2024/8/20 16:18
 * @description: 流程部署实现类
 */
@Slf4j
@Component
public class FlowDeploymentServiceImpl extends ServiceImpl<FlowDeploymentMapper, FlowReDeployment> implements FlowDeploymentService {

    @Inject
    private FlowActModelService modelService;
    @Inject
    private RepositoryService repositoryService;
    @Inject
    private RuntimeService runtimeService;
    @Inject
    private ProcessDiagramGenerator processDiagramGenerator;

    @Tran
    @Override
    public String deploymentModel(String modelId) {
        FlowActModel actModel = modelService.getById(modelId.trim());
        if (actModel == null) {
            throw new SuperbException("未找到流程模型，无法进行流程部署！");
        }
        if (StringUtils.isBlank(actModel.getModelEditorXml())) {
            throw new SuperbException("未进行流程设计，无法进行部署！");
        }
        UpdateChain.of(FlowActModel.class)
                .setRaw(FlowActModel::getVersion, "VERSION_ + 1")
                .setRaw(FlowActModel::getRev, "REV_ + 1")
                .where(FlowActModel::getId).eq(modelId)
                .update();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(actModel.getModelEditorXml().getBytes(StandardCharsets.UTF_8));
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream(actModel.getKey() + ".bpmn", inputStream)
                .name(actModel.getName())
                .key(actModel.getKey())
                .tenantId(HeadersUtils.getTenantId())
                .category(actModel.getModelType())
                .deploy();
        return deployment.getId();
    }

    @Override
    public void deleteDeployment(String deployId, Boolean cascade) {
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deployId).deploymentTenantId(HeadersUtils.getTenantId()).singleResult();
        if (deployment == null) {
            throw new SuperbException("未查询到对应的流程实例，无法进行删除！");
        }
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().deploymentId(deployId).list();
        if (!list.isEmpty()) {
            throw new SuperbException("当前流程定义存在运行中的实例，无法删除！");
        }
        try {
            repositoryService.deleteDeployment(deployId, cascade);
        } catch (Exception e) {
            log.error("删除流程实例：{}-----失败，原因：{}", deployId, e.getMessage());
        }
    }

    /**
     * @param processDefinitionId 流程定义ID
     * @Returns: {@link String}
     * @author mojie
     * @date: 2024/8/21 13:41
     * @description: 流程状态设置
     */
    @Override
    public String deploymentState(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).processDefinitionTenantId(HeadersUtils.getTenantId()).singleResult();
        if (processDefinition == null) {
            throw new SuperbException("未查询到对于的流程实例定义，无法进行操作！");
        }
        if (processDefinition.isSuspended()) {
            repositoryService.activateProcessDefinitionById(processDefinition.getId());
            return "激活成功！";
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId());
            return "终止成功！";
        }
    }

    /**
     * @param flowKey 流程定义标识
     * @Returns: {@link Process}
     * @author mojie
     * @date: 2024/8/22 13:27
     * @description: 获取流程定义XML信息
     */
    private Process getProcess(String flowKey) {
        // 获取部署在引擎中流程
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(flowKey).processDefinitionTenantId(HeadersUtils.getTenantId()).singleResult();
        if (processDefinition == null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "该流程不不存在，请部署后再试！");
        }
        // 获得流程信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        return bpmnModel.getMainProcess();
    }

    @Override
    public List<FlowUserTask> getAllFlowUserTask(String flowKey) {
        // 所有的流程节点
        List<UserTask> userTaskList = this.getProcess(flowKey).findFlowElementsOfType(UserTask.class);
        if (userTaskList.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlowUserTask> list = new ArrayList<>();
        for (UserTask userTask : userTaskList) {
            FlowUserTask flowUserTask = new FlowUserTask();
            flowUserTask.setId(userTask.getId());
            flowUserTask.setName(userTask.getName());
            flowUserTask.setKey(userTask.getFormKey());
            flowUserTask.setAssignee(userTask.getAssignee());
            flowUserTask.setCandidateUsers(userTask.getCandidateUsers());
            flowUserTask.setCandidateGroups(userTask.getCandidateGroups());
            flowUserTask.setFlowCustomProps(this.getTaskAttributes(userTask.getAttributes()));
            list.add(flowUserTask);
        }
        return list;

    }

    /**
     * @param attributes 流程变量
     * @Returns: {@link Map <String,Object>}
     * @author mojie
     * @date: 2024/8/21 14:38
     * @description: 获取任务变量
     */
    private Map<String, Object> getTaskAttributes(Map<String, List<ExtensionAttribute>> attributes) {
        Map<String, Object> map = new HashMap<>();
        attributes.forEach((key, value) -> map.put(key, value.get(0).getValue()));
        return map;
    }

    @Override
    public List<FlowElement> getFlowElements(String flowKey) {
        Process process = this.getProcess(flowKey);
        return process.getFlowElements().stream().toList();
    }

    @Override
    public FlowElement getFlowElementById(String flowKey, String elementId) {
        Process process = this.getProcess(flowKey);
        return process.getFlowElement(elementId);
    }

    @Override
    public void loadPng(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        InputStream inputStream = processDiagramGenerator.generateJpgDiagram(bpmnModel);
        Context ctx = ContextUtil.current();
        ctx.contentType("image/jpeg");
        ctx.output(inputStream);
    }
}

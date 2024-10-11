package com.superb.flowable.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.security.entity.SuperbUser;
import com.superb.common.security.utils.SuperbUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.dto.FlowProcessInstance;
import com.superb.flowable.api.dto.Option;
import com.superb.flowable.api.enums.FlowCommentType;
import com.superb.flowable.api.enums.FlowExecuteType;
import com.superb.flowable.api.vo.FlowComment;
import com.superb.flowable.api.vo.FlowStartParams;
import com.superb.flowable.service.FlowProcessInstanceService;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:49
 */
@Component
public class FlowProcessInstanceServiceImpl implements FlowProcessInstanceService {

    @Inject
    private RepositoryService repositoryService;
    @Inject
    private RuntimeService runtimeService;
    @Inject
    private TaskService taskService;
    @Inject
    private HistoryService historyService;

    @Override
    public List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow) {
        if (StringUtils.isBlank(key)) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "标识不能为空！");
        }
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (!HeadersUtils.getTenantId().equals("superb")) {
            processInstanceQuery.processInstanceTenantId(HeadersUtils.getTenantId());
        }
        List<ProcessInstance> processInstances;
        if (isFlow) {
            processInstances = processInstanceQuery
                    .processDefinitionKey(key)
                    .orderByStartTime()
                    .desc()
                    .list();
        } else {
            processInstances = processInstanceQuery.processInstanceBusinessKey(key).orderByStartTime().desc().list();
        }
        if (processInstances.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlowProcessInstance> list = new ArrayList<>();
        for (ProcessInstance processInstance : processInstances) {
            FlowProcessInstance flowProcessInstance = new FlowProcessInstance();
            flowProcessInstance.setProcessInstanceId(processInstance.getProcessInstanceId());
            flowProcessInstance.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            flowProcessInstance.setStartUserId(processInstance.getStartUserId());
            flowProcessInstance.setBusinessKey(processInstance.getBusinessKey());
            flowProcessInstance.setName(processInstance.getName());
            flowProcessInstance.setTenantId(processInstance.getTenantId());
            flowProcessInstance.setBusinessKeyStatus(processInstance.getBusinessStatus());
            flowProcessInstance.setDeploymentId(processInstance.getDeploymentId());
            flowProcessInstance.setProcessInstanceVersion(processInstance.getProcessDefinitionVersion());
            flowProcessInstance.setStatus(processInstance.isSuspended());
            list.add(flowProcessInstance);
        }
        return list;
    }

    @Override
    public String startProcessInstanceByKey(FlowStartParams startParams) {
        String flowKey = startParams.getFlowKey();
        if (StringUtils.isBlank(flowKey)) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "流程启动标识不能为空！");
        }
        String businessKey = startParams.getBusinessKey();
        if (StringUtils.isBlank(businessKey)) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "流程启动业务主键不能为空！");
        }
        if (StringUtils.isBlank(startParams.getProcessName())) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "流程名称不能为空！");
        }
        // 获取部署的最新版本流程
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(flowKey).processDefinitionTenantId(HeadersUtils.getTenantId()).latestVersion().singleResult();
        if (processDefinition == null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "未找到指定的流程【" + flowKey + "】,无法启动流程实例");
        }
        if (processDefinition.isSuspended()) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "当前流程【" + processDefinition.getName() + "】已终止，无法启动流程实例");
        }
        // 获取当前业务住建是否已存在运行实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if (processInstance != null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "当前任务已启动流程，无法再次启动流程!");
        }
        Map<String, Object> variables = startParams.getVariables();
        if (variables == null || variables.isEmpty()) {
            variables = new HashMap<>();
        }
        boolean skipFirstNode = startParams.isSkipFirstNode();
        // 如果自动跳过开始节点，需要设置第一步执行人为启动流程的人
        if (skipFirstNode) {
            variables.put("initiator", StpUtil.getLoginIdAsString());
        }
        // 设置流程发起人-当前登录人
        Authentication.setAuthenticatedUserId(StpUtil.getLoginIdAsString());
        // 用流程定义的KEY启动，会自动选择KEY相同的流程定义中最新版本的那个(KEY为模型中的流程唯一标识)
        ProcessInstance instance = runtimeService.startProcessInstanceByKeyAndTenantId(flowKey, businessKey, variables, HeadersUtils.getTenantId());
        // 设置流程名称
        runtimeService.setProcessInstanceName(instance.getProcessInstanceId(), startParams.getProcessName());
        // 更新业务状态-默认发起
        runtimeService.updateBusinessStatus(instance.getProcessInstanceId(), "start");
        // 如果设置自动跳过 需要将第一个节点班里人为发起人
        if (skipFirstNode) {
            TaskQuery taskQuery = taskService.createTaskQuery().active().taskAssignee(StpUtil.getLoginIdAsString()).processDefinitionKey(flowKey);
            // 设置流程实例id
            taskQuery.processInstanceId(instance.getId());
            Task task = taskQuery.singleResult();
            // 执行第一个节点任务
            if (task != null) {
                SuperbUser current = SuperbUtils.current();
                FlowComment flowComment = new FlowComment();
                flowComment.setAssignee(current.getId());
                flowComment.setAssigneeName(current.getNickname());
                flowComment.setTaskId(task.getId());
                flowComment.setProcessInstanceId(task.getProcessInstanceId());
                flowComment.setCommentContent(current.getNickname() + "发起流程申请");
                flowComment.setExecuteType(FlowExecuteType.START.getCode());
                flowComment.setExecuteTypeValue(FlowExecuteType.START.getInfo());
                flowComment.setFlowCommentType(FlowCommentType.APPROVE.getCode());
                // 当前任务添加备注
                taskService.addComment(task.getId(), instance.getProcessInstanceId(), FlowCommentType.NORMAL.getCode(), JSON.toJSONString(flowComment));
                // 执行下一步
                taskService.complete(task.getId());
            }
        }
        Authentication.setAuthenticatedUserId(null);
        // 将启动实例ID返回
        return instance.getId();
    }

    @Override
    public void updateProcessInstanceBusinessStatus(String processInstanceId, String status) {
        runtimeService.updateBusinessStatus(processInstanceId, status);
    }

    @Override
    public List<Option> getFlowBackUserTasks(String processInstanceId) {
        List<Option> list = new ArrayList<>();
        List<HistoricActivityInstance> userTask = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished() // 已经执行结束的节点
                .orderByHistoricActivityInstanceEndTime().asc() // 按执行结束时间排序
                .list();
        if (userTask.size() > 0) {
            // 排除网关 存在并行网关 执行会签完成--返回上一节点时，为网关
            for (HistoricActivityInstance historicActivityInstance : userTask) {
                if (!checkTaskGateway(historicActivityInstance)) {
                    list.add(new Option(historicActivityInstance.getActivityId(), historicActivityInstance.getActivityName()));
                }
            }
        }
        return list;
    }

    /**
     * @param historicActivityInstance
     * @Returns: {@link boolean}
     * @author mojie
     * @date: 2024/8/21 17:10
     * @description: 校验历史实例是否是网关
     */
    private boolean checkTaskGateway(HistoricActivityInstance historicActivityInstance){
        boolean isGateway = false;
        Process process = repositoryService.getBpmnModel(historicActivityInstance.getProcessDefinitionId()).getMainProcess();
        FlowNode flowNode = (FlowNode) process.getFlowElement(historicActivityInstance.getActivityId());
        //1. 判断该节点上一个节点是不是并行网关节点
        List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();
        if (!incomingFlows.isEmpty()) {
            for (SequenceFlow sequenceFlow : incomingFlows) {
                FlowElement upNode = sequenceFlow.getSourceFlowElement();
                if ((upNode instanceof ParallelGateway || upNode instanceof InclusiveGateway)) {
                    isGateway = true;
                }
            }
        }
        return isGateway;
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId) {
        List<FlowExecutionHistory> list = new ArrayList<>();
        // 根据流程实例ID获取流程历史信息
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        if (!activityInstanceList.isEmpty()) {
            // 获取历史审批材料
            List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
            for (HistoricActivityInstance instance : activityInstanceList) {
                FlowExecutionHistory executionHistory = new FlowExecutionHistory();
                executionHistory.setHistoryId(instance.getId());
                executionHistory.setTaskId(instance.getTaskId());
                executionHistory.setProcessDefinitionId(instance.getProcessDefinitionId());
                executionHistory.setExecutionId(instance.getExecutionId());
                executionHistory.setTaskName(instance.getActivityName());
                executionHistory.setStartTime(instance.getStartTime());
                executionHistory.setEndTime(instance.getEndTime());
                executionHistory.setDuration(instance.getDurationInMillis());
                executionHistory.setAssignee(instance.getAssignee());
                for (Comment comment : commentList) {
                    // 如果任务id相同，则将批注信息追加到历史中
                    if (instance.getTaskId().equals(comment.getTaskId())) {
                        executionHistory.setComment(JSON.parseObject(comment.getFullMessage(), FlowComment.class));
                    }
                }
                list.add(executionHistory);
            }
        }
        return list;
    }

    @Override
    public boolean updateProcessInstanceState(String processInstanceId) {
        // 获得实例信息
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "流程实例不存在或结束！");
        }
        try {
            if (processInstance.isSuspended()) {
                runtimeService.activateProcessInstanceById(processInstanceId);
            } else {
                runtimeService.suspendProcessInstanceById(processInstanceId);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

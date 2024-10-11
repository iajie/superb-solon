package com.superb.flowable.service.impl;

import com.alibaba.fastjson2.JSON;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.utils.StringUtils;
import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.enums.FlowCommentType;
import com.superb.flowable.api.enums.FlowExecuteType;
import com.superb.flowable.api.vo.FlowCancellation;
import com.superb.flowable.api.vo.FlowComment;
import com.superb.flowable.api.vo.FlowExecuteNextStep;
import com.superb.flowable.service.FlowTaskService;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 17:41
 */
@Component
public class FlowTaskServiceImpl implements FlowTaskService {

    @Inject
    private RuntimeService runtimeService;
    @Inject
    private TaskService taskService;
    @Inject
    private HistoryService historyService;

    @Override
    public void executeNextStep(FlowExecuteNextStep executeNextStep) {
        if (StringUtils.isBlank(executeNextStep.getTaskId())) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "任务ID不能为空");
        }
        Task task = this.getFlowTask(executeNextStep.getTaskId());
        if (task == null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "任务不存在或已被处理！");
        }
        // 是否执行回退初始节点
        if (executeNextStep.isInit()) {
            executeNextStep.setExecuteType(FlowExecuteType.REVOCATION);
        }
        // 任务执行
        switch (executeNextStep.getExecuteType()) {
            case REVOCATION -> this.revocation(task, executeNextStep);
            case RESUBMIT, AGREE -> this.complete(task, executeNextStep);
            case REJECT -> this.reject(task, executeNextStep);
            case REJECT_TO_TASK -> this.rejectToTask(task, executeNextStep);
        };
    }

    /**
     * @param task 执行任务对象
     * @param executeNextStep 执行参数
     * @author mojie
     * @date: 2024/8/22 9:24
     * @description: 组装审批意见
     */
    private void saveFlowComment(Task task, FlowExecuteNextStep executeNextStep) {
        // 组装审批信息，并保存
        FlowComment flowComment = new FlowComment();
        flowComment.setTaskId(executeNextStep.getTaskId());
        flowComment.setAssignee(executeNextStep.getAssignee());
        flowComment.setAssigneeName(executeNextStep.getAssigneeName());
        flowComment.setTaskKey(task.getTaskDefinitionKey());
        flowComment.setProcessInstanceId(task.getProcessInstanceId());
        flowComment.setCommentContent(executeNextStep.getCommentContent());
        flowComment.setExecuteType(executeNextStep.getExecuteType().getCode());
        flowComment.setExecuteTypeValue(executeNextStep.getExecuteType().getInfo());
        flowComment.setFlowCommentType(executeNextStep.getFlowCommentType().getCode());
        flowComment.setVariables(executeNextStep.getVariables());
        if (executeNextStep.isComment()) {
            this.addComment(flowComment);
        }
    }

    /**
     * @param processInstanceId 流程实例ID
     * @author mojie
     * @date: 2024/8/22 11:22
     * @description: 校验流程状态
     */
    private void checkFlowable(String processInstanceId) {
        // 先获取当前任务是否还在运行
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "当前流程不存在或已完成，无法进行操作");
        }
        if (processInstance.isSuspended()) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "当前流程已终止，需激活流程后操作");
        }
    }

    /**
     * @param task 任务对象
     * @param executeNextStep 执行参数
     * @author mojie
     * @date: 2024/8/22 8:58
     * @description: 往下执行用户任务
     */
    private void complete(Task task, FlowExecuteNextStep executeNextStep) {
        this.checkFlowable(task.getProcessInstanceId());
        // 增加审批意见
        executeNextStep.setFlowCommentType(FlowCommentType.APPROVE);
        this.saveFlowComment(task, executeNextStep);
        // 设置当前任务执行人
        taskService.setAssignee(task.getId(), executeNextStep.getAssignee());
        // 执行任务
        taskService.complete(task.getId(), executeNextStep.getVariables());
    }

    /**
     * @param task 任务对象
     * @param executeNextStep 执行参数
     * @author mojie
     * @date: 2024/8/22 9:01
     * @description: 申请人撤回申请-回到发起任务节点
     */
    private void revocation(Task task, FlowExecuteNextStep executeNextStep) {
        this.checkFlowable(task.getProcessInstanceId());
        // 获取所有任务节点
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 发起申请任务节点
        String startTaskKey = taskInstances.get(0).getTaskDefinitionKey();
        // 增加审批意见
        executeNextStep.setFlowCommentType(FlowCommentType.REVOCATION);
        this.saveFlowComment(task, executeNextStep);
        // 撤回任务到初始节点
        runtimeService.createChangeActivityStateBuilder()
                // 流程实例
                .processInstanceId(task.getProcessInstanceId())
                // 当前任务节点-->指定任务节点
                .moveActivityIdTo(task.getTaskDefinitionKey(), startTaskKey)
                .changeState();
    }

    /**
     * @param task 任务对象
     * @param executeNextStep 执行参数
     * @author mojie
     * @date: 2024/8/22 9:01
     * @description: 驳回任务
     */
    private void reject(Task task, FlowExecuteNextStep executeNextStep) {
        this.checkFlowable(task.getProcessInstanceId());
        // 添加审批意见
        executeNextStep.setFlowCommentType(FlowCommentType.REJECT);
        this.saveFlowComment(task, executeNextStep);
        // 获取上次执行节点
        String upNodeKey = this.getUpNodeKey(task.getId());
        // 驳回到上一节点
        taskService.setAssignee(task.getId(), executeNextStep.getAssignee());
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), upNodeKey)
                .changeState();
    }

    /**
     * @param task 任务对象
     * @param executeNextStep 执行参数
     * @author mojie
     * @date: 2024/8/22 9:01
     * @description: 驳回任务到指定节点
     */
    private void rejectToTask(Task task, FlowExecuteNextStep executeNextStep) {
        this.checkFlowable(task.getProcessInstanceId());
        if (StringUtils.isBlank(executeNextStep.getRejectToTaskId())) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "未指定驳回的任务ID，操作失败！");
        }
        executeNextStep.setFlowCommentType(FlowCommentType.REJECT);
        this.saveFlowComment(task, executeNextStep);
        taskService.setAssignee(task.getId(), executeNextStep.getAssignee());
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), executeNextStep.getRejectToTaskId())
                .changeState();
    }


    @Override
    public void addComment(FlowComment flowComment) {
        // 流程全局线程信息
        Authentication.setAuthenticatedUserId(flowComment.getAssignee());
        // 将审批意见转json存
        String message = JSON.toJSONString(flowComment);
        taskService.addComment(flowComment.getTaskId(), flowComment.getProcessInstanceId(), flowComment.getFlowCommentType(), message);
        // 清除线程数据
        Authentication.setAuthenticatedUserId(null);
    }

    @Override
    public Task getFlowTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "未查询到任务信息，操作中断！");
        }
        return task;
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId, String assignee) {
        List<FlowExecutionHistory> list = new ArrayList<>();
        Task flowTask = this.getFlowTask(taskId);
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        if (StringUtils.isNotBlank(assignee)) {
            historicActivityInstanceQuery.taskAssignee(assignee);
        }
        // 根据任务
        List<HistoricActivityInstance> historicTaskInstances = historicActivityInstanceQuery
                .processInstanceId(flowTask.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().asc().list();
        if (!historicTaskInstances.isEmpty()) {
            // 获取历史审批材料
            List<Comment> commentList = taskService.getTaskComments(taskId);
            for (HistoricActivityInstance instance : historicTaskInstances) {
                FlowExecutionHistory executionHistory = new FlowExecutionHistory();
                executionHistory.setHistoryId(instance.getId());
                executionHistory.setTaskId(taskId);
                executionHistory.setProcessDefinitionId(flowTask.getProcessDefinitionId());
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
    public List<String> getUserTaskExecutorList(String taskId, boolean isMainer, boolean isGroup) {
        List<String> list = new ArrayList<>();
        Task flowTask = this.getFlowTask(taskId);
        if (isMainer) {
            list.add(flowTask.getAssignee());
        } else {
            // 获取候选人信息
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
            if (!identityLinks.isEmpty()) {
                for (IdentityLink identityLink : identityLinks) {
                    // 不为候选都跳过
                    if (!IdentityLinkType.CANDIDATE.equalsIgnoreCase(identityLink.getType())) {
                        continue;
                    }
                    if (isGroup) {
                        // candidate候选人类型 用户id不为空
                        if (StringUtils.isNotBlank(identityLink.getGroupId()) && !list.contains(identityLink.getGroupId())) {
                            list.add(identityLink.getGroupId());
                        }
                    } else {
                        // candidate候选人类型 用户id不为空
                        if (StringUtils.isNotBlank(identityLink.getUserId()) && !list.contains(identityLink.getUserId())) {
                            list.add(identityLink.getUserId());
                        }
                    }

                }
            }
        }
        return list;
    }

    @Override
    public void cancellationProcessInstance(List<FlowCancellation> cancellations) {
        if (cancellations.isEmpty()) {
            throw new SuperbException(SuperbCode.FLOWABLE_ERROR, "没有作废的流程实例");
        }
        for (FlowCancellation cancellation : cancellations) {
            FlowComment flowComment = new FlowComment();
            flowComment.setAssignee(cancellation.getAssignee());
            flowComment.setAssigneeName(cancellation.getAssigneeName());
            flowComment.setTaskId(cancellation.getTaskId());
            flowComment.setProcessInstanceId(cancellation.getProcessInstanceId());
            flowComment.setCommentContent(cancellation.getCancellationCause());
            flowComment.setExecuteType(FlowExecuteType.CANCELLATION.getCode());
            flowComment.setExecuteTypeValue(FlowExecuteType.CANCELLATION.getInfo());
            flowComment.setFlowCommentType(FlowCommentType.CANCELLATION.getCode());
            this.addComment(flowComment);
            runtimeService.deleteProcessInstance(cancellation.getProcessInstanceId(), cancellation.getCancellationCause());
        }
    }

    @Override
    public String getUpNodeKey(String taskId) {
        // 获取历史任务节点
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskId(taskId).finished().orderByHistoricTaskInstanceEndTime().desc().list();
        HistoricTaskInstance instance = list.get(0);
        return instance.getTaskDefinitionKey();
    }
}

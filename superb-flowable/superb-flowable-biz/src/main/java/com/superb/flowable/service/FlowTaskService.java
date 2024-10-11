package com.superb.flowable.service;

import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.vo.FlowCancellation;
import com.superb.flowable.api.vo.FlowComment;
import com.superb.flowable.api.vo.FlowExecuteNextStep;
import org.flowable.task.api.Task;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:20
 */
public interface FlowTaskService {

    /**
     * @param executeNextStep 流程执行参数
     * @Returns: {@link boolean} 执行结果是否成功
     * @author mojie
     * @date: 2024/8/21 16:28
     * @description: 流程任务执行下一步
     */
    void executeNextStep(FlowExecuteNextStep executeNextStep);

    /**
     * @param flowComment 审批信息
     * @author mojie
     * @date: 2024/8/21 16:29
     * @description: 添加审批意见
     */
    void addComment(FlowComment flowComment);

    /**
     * @param taskId 任务ID
     * @Returns: {@link Task}
     * @author mojie
     * @date: 2024/8/21 16:30
     * @description: 获取流程任务对象
     */
    Task getFlowTask(String taskId);

    /**
     * @param taskId 任务ID
     * @Returns: {@link List<FlowExecutionHistory>}
     * @author mojie
     * @date: 2024/8/21 16:36
     * @description: 根据任务ID获取流程执行历史记录
     */
    default List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId) {
        return getFlowExecutionHistoryList(taskId, null);
    }

    /**
     * @param taskId 任务ID
     * @param assignee 用户ID
     * @Returns: {@link List<FlowExecutionHistory>}
     * @author mojie
     * @date: 2024/8/21 16:36
     * @description: 根据任务ID和用户ID获取流程用户执行历史记录
     */
    List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId, String assignee);

    /**
     * @param taskId 任务ID
     * @Returns: {@link List<String>}
     * @author mojie
     * @date: 2024/8/21 16:41
     * @description: 获取当前任务节点执行人
     */
    default String getUserTaskExecutor(String taskId) {
        return getUserTaskExecutorList(taskId, true, false).get(0);
    }

    /**
     * @param taskId 任务ID
     * @param isMainer 是否为执行人
     * @Returns: {@link List<String>}
     * @author mojie
     * @date: 2024/8/21 16:41
     * @description: 获取当前任务节点执行人(候选人)
     */
    default List<String> getUserTaskExecutors(String taskId, boolean isMainer) {
        return getUserTaskExecutorList(taskId, false, false);
    }

    /**
     * @param taskId 任务ID
     * @Returns: {@link List<String>}
     * @author mojie
     * @date: 2024/8/21 16:42
     * @description: 获取当前任务节点候选部门
     */
    default List<String> getUserTaskOrganIds(String taskId) {
        return getUserTaskExecutorList(taskId, false, true);
    }

    /**
     * @param taskId 任务ID
     * @param isMainer 是否为执行人
     * @param isGroup 是否为候选组
     * @Returns: {@link List<String>}
     * @author mojie
     * @date: 2024/8/21 16:41
     * @description: 获取当前任务节点执行人(候选人)
     */
    List<String> getUserTaskExecutorList(String taskId, boolean isMainer, boolean isGroup);

    /**
     * @param cancellations
     * @Returns: {@link boolean}
     * @author mojie
     * @date: 2024/8/21 16:53
     * @description: 流程作废
     */
    default void cancellationProcessInstance(FlowCancellation...cancellations) {
        cancellationProcessInstance(Arrays.stream(cancellations).toList());
    }

    /**
     * @param cancellations 流程
     * @Returns: {@link boolean}
     * @author mojie
     * @date: 2024/8/21 16:53
     * @description: 批量流程作废
     */
    void cancellationProcessInstance(List<FlowCancellation> cancellations);


    /**
     * @param processInstanceId 流程实例ID
     * @Returns: {@link String}
     * @author mojie
     * @date: 2024/8/21 16:47
     * @description: 根据流程实例ID获取上一个用户任务节点KEY
     */
    String getUpNodeKey(String processInstanceId);
}

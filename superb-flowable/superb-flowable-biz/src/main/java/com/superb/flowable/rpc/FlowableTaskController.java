package com.superb.flowable.rpc;

import com.superb.flowable.api.constants.Constants;
import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.remote.RemoteFlowableTaskService;
import com.superb.flowable.api.vo.FlowCancellation;
import com.superb.flowable.api.vo.FlowComment;
import com.superb.flowable.api.vo.FlowExecuteNextStep;
import com.superb.flowable.service.FlowTaskService;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:18
 */
@Remoting
@Mapping(Constants.TASK_PATH)
public class FlowableTaskController implements RemoteFlowableTaskService {

    @Inject
    private FlowTaskService taskService;

    @Override
    public void executeNextStep(FlowExecuteNextStep executeNextStep) {
        taskService.executeNextStep(executeNextStep);
    }

    @Override
    public void addComment(FlowComment flowComment) {
        taskService.addComment(flowComment);
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId, String assignee) {
        return taskService.getFlowExecutionHistoryList(taskId, assignee);
    }

    @Override
    public List<String> getUserTaskExecutorList(String taskId, boolean isMainer, boolean isGroup) {
        return taskService.getUserTaskExecutorList(taskId, isMainer, isGroup);
    }

    @Override
    public void cancellationProcessInstance(List<FlowCancellation> cancellations) {
        taskService.cancellationProcessInstance(cancellations);
    }
}

package com.superb.flowable.service;

import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.dto.FlowProcessInstance;
import com.superb.flowable.api.dto.Option;
import com.superb.flowable.api.vo.FlowStartParams;

import java.util.List;
import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:47
 */
public interface FlowProcessInstanceService {

    /**
     * @param flowKey 流程标识
     * @Returns: {@link List<FlowProcessInstance>}
     * @author mojie
     * @date: 2024/8/22 11:00
     * @description: 根据流程标识获取所有运行中的流程实例
     */
    default List<FlowProcessInstance> getFlowInstanceList(String flowKey) {
        return getFlowInstanceList(flowKey, true);
    }

    /**
     * @param key 标识
     * @param isFlow 是否为流程，否则为业务主键
     * @Returns: {@link List<FlowProcessInstance>}
     * @author mojie
     * @date: 2024/8/22 10:28
     * @description: 根据流程标识获取所有运行中的流程实例
     */
    List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow);

    /**
     * @param startParams-flowKey 流程标识
     * @param startParams-businessKey 业务主键
     * @param startParams-variables 流程变量
     * @param startParams-isSkipFirstNode 是否跳过开始节点
     * @Returns: {@link String}
     * @author mojie
     * @date: 2024/8/21 14:41
     * @description: 启动流程实例
     */
    String startProcessInstanceByKey(FlowStartParams startParams);

    /**
     * @param processInstanceId 流程实例ID
     * @param status 状态
     * @author mojie
     * @date: 2024/8/22 10:46
     * @description: 更新流程实例中业务的状态
     */
    void updateProcessInstanceBusinessStatus(String processInstanceId, String status);

    /**
     * @param businessKey 业务主键
     * @Returns: {@link FlowProcessInstance}
     * @author mojie
     * @date: 2024/8/22 10:47
     * @description: 根据业务主键获取流程实例
     */
    default FlowProcessInstance getProcessInstance(String businessKey) {
        return getFlowInstanceList(businessKey, false).get(0);
    }

    /**
     * @param processInstanceId 流程实例ID
     * @Returns: {@link Map<String,Object>}
     * @author mojie
     * @date: 2024/8/21 16:38
     * @description: 执行流程可回退的用户任务节点
     */
    List<Option> getFlowBackUserTasks(String processInstanceId);

    /**
     * @param processInstanceId 流程实例ID
     * @Returns: {@link List< FlowExecutionHistory >}
     * @author mojie
     * @date: 2024/8/21 16:40
     * @description: 获取流程执行历史记录
     */
    List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId);

    /**
     * @param processInstanceId 实例ID
     * @Returns: {@link boolean}
     * @author mojie
     * @date: 2024/8/22 13:01
     * @description: 修改实例状态
     */
    boolean updateProcessInstanceState(String processInstanceId);
}

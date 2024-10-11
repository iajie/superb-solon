package com.superb.flowable.api.remote;

import com.superb.common.security.filter.NamiFilter;
import com.superb.flowable.api.constants.Constants;
import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.dto.FlowProcessInstance;
import com.superb.flowable.api.dto.Option;
import com.superb.flowable.api.vo.FlowStartParams;
import org.noear.nami.annotation.NamiClient;

import java.util.List;
import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 15:38
 * 流程运行实例管理-远程接口
 */
@NamiClient(name = "superb-flowable-biz", path = Constants.PROCESS_PATH)
public interface RemoteFlowableProcessService extends NamiFilter {

    /**
     * @param startParams-flowKey 流程标识
     * @param startParams-businessKey 业务主键
     * @param startParams-variables 流程变量
     * @param startParams-isSkipFirstNode 是否跳过开始节点
     * @Returns: {@link String} 流程实例ID
     * @author ajie
     * @date: 2024/8/21 15:40
     * @description: 启动流程
     */
    String startProcessInstanceByKey(FlowStartParams startParams);

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
     * @Returns: {@link List<  FlowExecutionHistory  >}
     * @author mojie
     * @date: 2024/8/21 16:40
     * @description: 获取流程执行历史记录
     */
    List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId);

    /**
     * @param businessKey 业务主键
     * @Returns: {@link FlowProcessInstance}
     * @author mojie
     * @date: 2024/8/22 10:47
     * @description: 根据业务主键获取流程实例
     */
    FlowProcessInstance getProcessInstance(String businessKey);

    /**
     * @param processInstanceId 流程实例ID
     * @param status 状态
     * @author mojie
     * @date: 2024/8/22 10:46
     * @description: 更新流程实例中业务的状态
     */
    void updateProcessInstanceBusinessStatus(String processInstanceId, String status);
}

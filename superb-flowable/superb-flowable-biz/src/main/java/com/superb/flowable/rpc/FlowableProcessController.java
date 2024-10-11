package com.superb.flowable.rpc;

import com.superb.flowable.api.constants.Constants;
import com.superb.flowable.api.dto.FlowExecutionHistory;
import com.superb.flowable.api.dto.FlowProcessInstance;
import com.superb.flowable.api.dto.Option;
import com.superb.flowable.api.remote.RemoteFlowableProcessService;
import com.superb.flowable.api.vo.FlowStartParams;
import com.superb.flowable.service.FlowProcessInstanceService;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 15:42
 */
@Remoting
@Mapping(Constants.PROCESS_PATH)
public class FlowableProcessController implements RemoteFlowableProcessService {

    @Inject
    private FlowProcessInstanceService processInstanceService;

    @Override
    public String startProcessInstanceByKey(FlowStartParams startParams) {
        return processInstanceService.startProcessInstanceByKey(startParams);
    }

    @Override
    public List<Option> getFlowBackUserTasks(String processInstanceId) {
        return processInstanceService.getFlowBackUserTasks(processInstanceId);
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId) {
        return processInstanceService.getFlowExecutionHistoryList(processInstanceId);
    }

    @Override
    public FlowProcessInstance getProcessInstance(String businessKey) {
        return processInstanceService.getProcessInstance(businessKey);
    }

    @Override
    public void updateProcessInstanceBusinessStatus(String processInstanceId, String status) {
        processInstanceService.updateProcessInstanceBusinessStatus(processInstanceId, status);
    }
}

package com.superb.flowable.service;

import com.mybatisflex.core.service.IService;
import com.superb.flowable.api.dto.FlowUserTask;
import com.superb.flowable.api.entity.FlowReDeployment;
import org.flowable.bpmn.model.FlowElement;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-20 16:17
 */
public interface FlowDeploymentService extends IService<FlowReDeployment> {

    /**
     * @author: ajie
     * @date: 2024/8/21 11:12
     * @description: 通过模型id部署流程
     */
    String deploymentModel(String modelId);

    /**
     * @author: ajie
     * @date: 2024/8/21 11:48
     * @description: 删除流程实例
     */
    void deleteDeployment(String deployId, Boolean cascade);

    /**
     * @author: ajie
     * @date: 2024/8/21 11:53
     * @description: 设置流程状态
     */
    String deploymentState(String processDefinitionId);


    /**
     * @param flowKey 流程定义标识
     * @Returns: {@link List < FlowUserTask >}
     * @author mojie
     * @date: 2024/8/21 14:02
     * @description: 获取用户任务列表
     */
    List<FlowUserTask> getAllFlowUserTask(String flowKey);

    /**
     * @param flowKey 流程标识
     * @Returns: {@link List<  FlowElement  >}
     * @author mojie
     * @date: 2024/8/21 15:54
     * @description: 获取流程实例中的所有元素节点
     */
    List<FlowElement> getFlowElements(String flowKey);

    /**
     * @param flowKey 流程标识
     * @param elementId 节点标识
     * @Returns: {@link FlowElement}
     * @author mojie
     * @date: 2024/8/21 15:54
     * @description: 获取流程实例中的所有元素节点
     */
    FlowElement getFlowElementById(String flowKey, String elementId);

    /**
     * @param processDefinitionId 定义ID
     * @Returns: 图片输出流
     * @author mojie
     * @date: 2024/8/23 9:40
     * @description: 加载部署的图片
     */
    void loadPng(String processDefinitionId);
}

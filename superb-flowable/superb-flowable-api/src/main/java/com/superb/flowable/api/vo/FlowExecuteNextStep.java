package com.superb.flowable.api.vo;

import com.superb.flowable.api.enums.FlowCommentType;
import com.superb.flowable.api.enums.FlowExecuteType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:21
 */
@Data
@ApiModel("流程执行请求参数")
public class FlowExecuteNextStep {

    @ApiModelProperty(value = "任务id", required = true)
    private String taskId;

    @ApiModelProperty(value = "执行类型", required = true)
    private FlowExecuteType executeType;

    @ApiModelProperty(value = "是否驳回倒流程发起任务，默认:false")
    private boolean isInit = false;

    @ApiModelProperty(value = "驳回到指定任务节点ID")
    private String rejectToTaskId;

    @ApiModelProperty(value = "审批意见")
    private String commentContent;

    @ApiModelProperty(value = "变量参数")
    private Map<String, Object> variables;

    @ApiModelProperty("审批人")
    private String assignee;

    @ApiModelProperty("审批人姓名")
    private String assigneeName;

    @ApiModelProperty("是否自动添加审批意见")
    private boolean isComment = true;

    @ApiModelProperty(hidden = true, value = "审批类型")
    private FlowCommentType flowCommentType;
}

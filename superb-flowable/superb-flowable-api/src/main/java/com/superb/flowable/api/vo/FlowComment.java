package com.superb.flowable.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 15:01
 */
@Data
@ApiModel("执行任务")
public class FlowComment {

    @ApiModelProperty(value = "流程定义Key")
    private String processInstanceId;

    @ApiModelProperty(value = "任务ID（缺省）")
    private String taskId;

    @ApiModelProperty(value = "任务定义KEY")
    private String taskKey;

    @ApiModelProperty(value = "类型")
    private String flowCommentType;

    @ApiModelProperty("审批人")
    private String assignee;

    @ApiModelProperty(value = "审批人姓名")
    private String assigneeName;

    @ApiModelProperty(value = "执行类型")
    private String executeType;

    @ApiModelProperty(value = "执行类型[agree：同意（审批通过）、reject:驳回（默认驳回上一个任务）、rejectToTask:驳回到指定任务节点、revocation:撤回]")
    private String executeTypeValue;

    @ApiModelProperty(value = "审批意见")
    private String commentContent;

    @ApiModelProperty(value = "额外携带的内容")
    private String ext;

    @ApiModelProperty(value = "变量参数")
    private Map<String, Object> variables;

}

package com.superb.flowable.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:51
 */
@Data
@ApiModel("流程作废")
public class FlowCancellation {

    @ApiModelProperty("审批人")
    @NotBlank(message = "审批人不能为空")
    private String assignee;

    @ApiModelProperty(value = "审批人姓名")
    @NotBlank(message = "审批人姓名不能为空")
    private String assigneeName;

    @ApiModelProperty(value = "任务ID")
    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    @ApiModelProperty(value = "流程实例ID")
    @NotBlank(message = "流程实例ID不能为空")
    private String processInstanceId;

    @ApiModelProperty(value = "作废原因")
    @NotBlank(message = "作废原因不能为空")
    private String cancellationCause;

}

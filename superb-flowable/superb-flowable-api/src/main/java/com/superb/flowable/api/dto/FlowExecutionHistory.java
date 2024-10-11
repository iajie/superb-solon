package com.superb.flowable.api.dto;

import com.superb.flowable.api.vo.FlowComment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 16:32
 */
@Data
@ApiModel("流程执行历史")
public class FlowExecutionHistory {

    @ApiModelProperty("历史ID")
    private String historyId;

    @ApiModelProperty("任务编号")
    private String taskId;

    @ApiModelProperty("任务执行编号")
    private String executionId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务Key")
    private String taskDefKey;

    @ApiModelProperty("流程部署ID")
    private String processDefinitionId;

    @ApiModelProperty("流程实例ID")
    private String procInsId;

    @ApiModelProperty("任务耗时(毫秒)")
    private Long duration;

    @ApiModelProperty("任务意见")
    private FlowComment comment;

    @ApiModelProperty("任务开始时间")
    private Date startTime;

    @ApiModelProperty("任务完成时间")
    private Date endTime;

    @ApiModelProperty("节点执行人")
    private String assignee;
}

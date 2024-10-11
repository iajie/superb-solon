package com.superb.flowable.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-22 10:22
 */
@Data
@ApiModel("运行实例")
public class FlowProcessInstance {

    @ApiModelProperty("流程实例ID")
    private String processInstanceId;

    @ApiModelProperty("流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty("流程定义ID")
    private Date startTime;

    @ApiModelProperty("发起人ID")
    private String startUserId;

    @ApiModelProperty("流程实例状态：true终止，false激活")
    private boolean status = false;

    @ApiModelProperty("业务主键")
    private String businessKey;

    @ApiModelProperty("实例名称")
    private String name;

    @ApiModelProperty("租户信息")
    private String tenantId;

    @ApiModelProperty("业务状态")
    private String businessKeyStatus;

    @ApiModelProperty("部署ID")
    private String deploymentId;

    @ApiModelProperty("流程实例运行版本")
    private Integer processInstanceVersion;
}

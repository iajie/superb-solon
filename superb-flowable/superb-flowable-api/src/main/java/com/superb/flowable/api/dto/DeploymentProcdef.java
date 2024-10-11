package com.superb.flowable.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 10:22
 */
@Data
@ApiModel("流程部署实例")
public class DeploymentProcdef {

    @ApiModelProperty("部署id")
    private String id;

    @ApiModelProperty("流程定义id")
    private String processDefinitionId;

    @ApiModelProperty("部署名称")
    private String name;

    @ApiModelProperty("流程标识")
    private String key;

    @ApiModelProperty("分类")
    private String modelType;

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("实例版本")
    private Integer version;

    @ApiModelProperty(value = "流程定义状态: 1:激活 , 2:中止")
    private Integer suspensionState;

    @ApiModelProperty(value = "是否存在开始节点formKey: 0：否 ,1:是")
    private Integer hasStartFormKey;

    @ApiModelProperty("部署时间")
    private Date deploymentTime;

}

package com.superb.flowable.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-20 16:22
 */
@Data
@ApiModel("流程部署")
@Table("act_re_deployment")
public class FlowReDeployment {

    @Column(value = "ID_")
    @ApiModelProperty("流程部署id")
    private String id;

    @Column(value = "NAME_")
    @ApiModelProperty("流程名称")
    private String name;

    @Column(value = "KEY_")
    @ApiModelProperty("流程key")
    private String flowKey;

    @Column(value = "CATEGORY_")
    @ApiModelProperty("流程分类")
    private String modelType;

    @Column(value = "ENGINE_VERSION_")
    @ApiModelProperty("流程引擎的版本")
    private String version;

    @Column(value = "DEPLOY_TIME_")
    @ApiModelProperty("部署时间")
    private Date deploymentTime;

    @Column(value = "TENANT_ID_")
    @ApiModelProperty("租户ID")
    private String tenantId;

    @Column(value = "DERIVED_FROM_")
    @ApiModelProperty("来源")
    private String derivedFrom;

    @Column(value = "DERIVED_FROM_ROOT_")
    @ApiModelProperty("来源")
    private String derivedFromRoot;

    @Column(value = "PARENT_DEPLOYMENT_ID_")
    @ApiModelProperty("父级部署ID")
    private String parentDeploymentId;

}

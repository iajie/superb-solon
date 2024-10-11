package com.superb.flowable.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-20 16:37
 */
@Data
@ApiModel("流程定义")
@Table("act_re_procdef")
public class ActReProcdef {

    @Column(value = "ID_")
    @ApiModelProperty(value = "流程定义ID")
    private String procdefId;

    @Column(value = "REV_")
    @ApiModelProperty(value = "版本号")
    private Integer rev = 0;

    @Column(value = "NAME_")
    @ApiModelProperty(value = "流程模型名称")
    private String name;

    @Column(value = "KEY_")
    @ApiModelProperty(value = "流程模型标识")
    private String key;

    @Column(value = "CATEGORY_")
    @ApiModelProperty(value = "分类", notes = "流程定义的Namespace就是类别")
    private String modelType;

    @Column(value = "VERSION_")
    @ApiModelProperty(value = "版本")
    private String version;

    @Column(value = "DEPLOYMENT_ID_")
    @ApiModelProperty(value = "部署ID")
    private String deploymentId;

    @Column(value = "RESOURCE_NAME_")
    @ApiModelProperty(value = "资源名称", notes = "流程bpmn文件名称")
    private String resourceName;

    @Column(value = "DGRM_RESOURCE_NAME_")
    @ApiModelProperty(value = "图片资源名称")
    private String dgrmResourceName;

    @Column(value = "DESCRIPTION_")
    @ApiModelProperty(value = "描述")
    private String description;

    @Column(value = "HAS_START_FORM_KEY_")
    @ApiModelProperty(value = "是否存在开始节点formKey: 0：否 ,1:是")
    private Integer hasStartFormKey;

    @Column(value = "HAS_GRAPHICAL_NOTATION_")
    @ApiModelProperty(value = "拥有图形信息")
    private Integer hasGraphicalNotation;

    @Column(value = "SUSPENSION_STATE_")
    @ApiModelProperty(value = "流程定义状态: 1:激活 , 2:中止")
    private Integer suspensionState;

    @Column(value = "TENANT_ID_")
    @ApiModelProperty("租户ID")
    private String tenantId;

    @Column(value = "ENGINE_VERSION_")
    @ApiModelProperty("流程引擎的版本")
    private String engineVersion;

    @Column(value = "DERIVED_FROM_")
    @ApiModelProperty("来源")
    private String derivedFrom;

    @Column(value = "DERIVED_FROM_ROOT_")
    @ApiModelProperty("来源")
    private String derivedFromRoot;

    @Column(value = "DERIVED_VERSION_")
    @ApiModelProperty("衍生版本")
    private String derivedVersion;
}

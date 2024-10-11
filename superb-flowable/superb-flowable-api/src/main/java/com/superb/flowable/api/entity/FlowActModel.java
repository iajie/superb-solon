package com.superb.flowable.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-12 09:16
 */
@Data
@Table("act_re_model")
@ApiModel(value = "流程定义模型")
@EqualsAndHashCode(callSuper = true)
public class FlowActModel extends BaseEntity {

    @Column(value = "ID_")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    @ApiModelProperty(value = "流程模型ID", notes = "流程模型ID")
    private String id;

    @Column(value = "NAME_")
    @ApiModelProperty(value = "流程模型名称", notes = "流程模型名称")
    private String name;

    @Column(value = "REV_")
    @ApiModelProperty(value = "版本号", notes = "流程模型版本")
    private Integer rev;

    @Column(value = "KEY_")
    @ApiModelProperty(value = "流程模型标识", notes = "流程模型标识")
    private String key;

    @Column(value = "CATEGORY_")
    @ApiModelProperty(value = "分类")
    private String modelType;

    @Column(value = "CREATE_TIME_")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column(value = "LAST_UPDATE_TIME_")
    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdateTime;

    @Column(value = "CREATE_BY_")
    @ApiModelProperty(value = "创建人", notes = "流程模型创建人")
    private String createBy;

    @Column(value = "VERSION_")
    @ApiModelProperty(value = "版本号", notes = "版本号")
    private Integer version = 0;

    @Column(value = "META_INFO_", isLarge = true)
    @ApiModelProperty(value = "BPMNXML内容", notes = "以XML形式保存的流程定义信息")
    private String modelEditorXml;

    @Column(value = "DEPLOYMENT_ID_")
    @ApiModelProperty(value = "部署id", notes = "部署id")
    private String deploymentId;

    @Column(value = "EDITOR_SOURCE_VALUE_ID_")
    @ApiModelProperty(value = "二进制文件id", notes = "设计器原始信息")
    private String editorSourceValueId;

    @Column(value = "EDITOR_SOURCE_EXTRA_VALUE_ID_")
    @ApiModelProperty(value = "二进制文件id", notes = "设计器拓展信息")
    private String editorSourceExtraValueId;

    @Column(value = "TENANT_ID_")
    @ApiModelProperty(value = "租户ID", notes = "租户ID")
    private String tenantId;

    @Column(value = "REMARKS_")
    @ApiModelProperty(value = "备注", notes = "备注")
    private String remarks;

    @Column(value = "ORGAN_ID_")
    private String organId;

    @Column(ignore = true)
    private Integer del;

}

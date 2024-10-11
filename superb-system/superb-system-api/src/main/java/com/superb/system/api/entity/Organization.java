package com.superb.system.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;

/**
 * 部门机构
 * @Author: ajie
 * @CreateTime: 2024-6-14
 */
@Data
@NoArgsConstructor
@ApiModel(value = "部门机构")
@Table(value = "system_organization")
@EqualsAndHashCode(callSuper=false)
public class Organization extends BaseEntity {

    @ApiModelProperty(value = "父机构ID")
    private String parentId;

    @ApiModelProperty(value = "父级标记", notes = "0最高级；1租户；2公司")
    private Integer parentCode;

    @ApiModelProperty(value = "机构编码", notes = "自定义")
    private String customCode;

    @NotBlank(message = "机构名称不能为空")
    @ApiModelProperty(value = "机构/部门名称全称", required = true)
    private String name;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @NotBlank(message = "区域编码不能为空")
    @ApiModelProperty(value = "区域编码", notes = "行政区域编码，和数据权限挂钩", required = true)
    private String areaCode;

    @ApiModelProperty(value = "区域名称", notes = "行政区域名称")
    private String areaName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "经度")
    private Double longitude;

    @ApiModelProperty(value = "纬度")
    private Double latitude;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型", notes = "1公司；2部门；3岗位", required = true)
    private Integer type;

    @ApiModelProperty(hidden = true)
    @Column(ignore = true)
    private String organId;

    @Column(ignore = true)
    @ApiModelProperty(value = "子级")
    private List<Organization> children;

    public Organization(String id, Integer del) {
        super(id, del);
    }
}
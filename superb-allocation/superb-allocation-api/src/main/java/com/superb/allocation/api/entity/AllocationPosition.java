package com.superb.allocation.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.List;

/**
 * 行政区划
 * @Author: ajie
 * @CreateTime: 2024-7-1
 */
@Data
@Table("allocation_position")
@ApiModel(value = "行政区划")
@EqualsAndHashCode(callSuper=false)
public class AllocationPosition extends BaseEntity {

    @Id
    @NotBlank(message = "区划CODE不能为空")
    @ApiModelProperty(value = "行政区划code", required = true)
    private String id;

    @NotBlank(message = "区划名称不能为空")
    @ApiModelProperty(value = "区划名称", required = true)
    private String name;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @NotBlank(message = "父级区划不能为空")
    @ApiModelProperty(value = "父级区划", required = true)
    private String parentId;

    @ApiModelProperty(value = "级别", notes = "1省级2市级3区县级4乡镇街道5居委会")
    private Integer level;

    @ApiModelProperty(value = "省级区划")
    private String parentTop;

    @ApiModelProperty(value = "经度")
    private Double longitude;

    @ApiModelProperty(value = "纬度")
    private Double latitude;

    @Column(ignore = true)
    @ApiModelProperty(value = "子级行政区划")
    private List<AllocationPosition> children;

    @Column(ignore = true)
    @ApiModelProperty(hidden = true)
    private String organId;
}
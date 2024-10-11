package com.superb.allocation.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 数据字典
 *
 * @Author: ajie
 * @CreateTime: 2024-6-18
 */
@Data
@Table("allocation_dict")
@ApiModel(value = "数据字典")
@EqualsAndHashCode(callSuper = false)
public class AllocationDict extends BaseEntity {

    @NotBlank(message = "类型名称不能为空")
    @ApiModelProperty(value = "类型名称", required = true)
    private String name;

    @NotBlank(message = "字典类型不能为空")
    @ApiModelProperty(value = "字典类型", required = true)
    private String code;

    @ApiModelProperty(value = "状态", notes = "0启用1禁用")
    private Integer status;

    @ApiModelProperty(value = "字典类型")
    @NotNull(message = "字典类型不能为空")
    private String dictType;

    @ApiModelProperty(value = "类型", notes = "0主要1信息2危险3警告4成功", required = true)
    private Integer type;

    @ApiModelProperty(value = "排序")
    private String sort;

    @ApiModelProperty(hidden = true)
    @Column(ignore = true)
    private String organId;
}
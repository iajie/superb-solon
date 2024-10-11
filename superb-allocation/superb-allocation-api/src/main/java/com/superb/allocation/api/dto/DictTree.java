package com.superb.allocation.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-28 10:36
 */
@Data
@ApiModel(value = "树形字典")
public class DictTree {

    @ApiModelProperty(value = "标题")
    private String label;

    @ApiModelProperty(value = "选中值")
    private String value;

    @ApiModelProperty(value = "状态", notes = "是否禁用")
    private boolean disabled;

    @ApiModelProperty(value = "子级")
    private List<DictTree> children;

    @ApiModelProperty(hidden = true)
    private String id;
}

package com.superb.allocation.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-28 10:36
 */
@Data
@ApiModel(value = "普通字典")
public class Dict {

    @ApiModelProperty(value = "标题")
    private String label;

    @ApiModelProperty(value = "选中值")
    private String value;

    @ApiModelProperty(value = "状态", notes = "是否禁用")
    private boolean disabled;

    @ApiModelProperty(value = "字典状态")
    private Integer type;

}

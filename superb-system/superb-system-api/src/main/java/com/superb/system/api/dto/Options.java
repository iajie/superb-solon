package com.superb.system.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-04 10:55
 */
@Data
@ApiModel(value = "列表")
public class Options {

    @ApiModelProperty(value = "名称")
    private String label;

    @ApiModelProperty(value = "值")
    private String value;

}

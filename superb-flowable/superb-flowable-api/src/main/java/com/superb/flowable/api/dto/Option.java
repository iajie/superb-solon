package com.superb.flowable.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-22 13:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("通用列表")
public class Option {

    @ApiModelProperty("显示值")
    private String label;

    @ApiModelProperty("实际值")
    private String value;

}

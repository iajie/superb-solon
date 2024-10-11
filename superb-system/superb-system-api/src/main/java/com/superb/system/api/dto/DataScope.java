package com.superb.system.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-09 14:48
 */
@Data
@ApiModel(value = "数据权限")
public class DataScope {

    @ApiModelProperty(value = "部门名称")
    private String organName;

    @ApiModelProperty(value = "部门id")
    private String organId;

    @ApiModelProperty(value = "数据权限范围", notes = "0本人；1全部；2本部门；3本部门及子部门；4自定义")
    private Integer dataScopeType;

    @ApiModelProperty(value = "是否默认", notes = "0否；1是")
    private Integer isMain;

}

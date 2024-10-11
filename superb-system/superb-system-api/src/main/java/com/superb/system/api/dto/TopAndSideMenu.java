package com.superb.system.api.dto;

import com.superb.system.api.entity.SystemPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-29 18:00
 */
@Data
@ApiModel(value = "菜单")
public class TopAndSideMenu {

    @ApiModelProperty(value = "顶部菜单")
    private List<SystemPermission> top;

    @ApiModelProperty(value = "侧边栏菜单")
    private List<SystemPermission> side;

}

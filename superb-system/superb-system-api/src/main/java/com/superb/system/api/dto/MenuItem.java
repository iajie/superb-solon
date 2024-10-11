package com.superb.system.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-28 17:17
 */
@Data
@ApiModel(value = "菜单")
public class MenuItem {

    @ApiModelProperty(hidden = true)
    private String id;

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "菜单路由")
    private String path;

    @ApiModelProperty(value = "组件名称")
    private String component;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @JsonIgnore
    @ApiModelProperty(value = "父级菜单id")
    private String parentId;

    @ApiModelProperty(value = "外链", notes = "0否；1是")
    private Integer outerChain;

    @ApiModelProperty(value = "是否显示", notes = "0启用；1禁用")
    private boolean hideInMenu;

    @ApiModelProperty(value = "菜单属性", notes = "0父级菜单；1子菜单；2权限")
    private Integer menuType;

    @ApiModelProperty(value = "子菜单")
    private List<MenuItem> children;
}

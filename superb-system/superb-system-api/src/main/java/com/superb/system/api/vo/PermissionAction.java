package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-04 11:21
 */
@Data
@ApiModel(value = "权限操作")
public class PermissionAction {

    @ApiModelProperty(value = "业务主键，编辑时必填")
    private String id;

    @NotBlank(message = "父级菜单不能为空")
    @ApiModelProperty(value = "父级菜单id", required = true)
    private String parentId;

    @NotBlank(message = "权限名称不能为空")
    @ApiModelProperty(value = "权限名称", required = true)
    private String name;

    @NotBlank(message = "权限代码不能为空")
    @ApiModelProperty(value = "权限代码", required = true)
    private String perms;

    @NotBlank(message = "权限类型不能为空")
    @ApiModelProperty(value = "权限类型")
    private String type;

    @NotNull(message = "排序不能为空")
    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    @ApiModelProperty(value = "备注")
    private String remarks;
}

package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-02 09:15
 */
@Data
@ApiModel(value = "权限分配")
public class Distribution {

    @NotBlank(message = "角色不能为空")
    @ApiModelProperty(value = "角色id", required = true)
    private String roleId;

    @NotNull(message = "权限集合参数为必填")
    @ApiModelProperty(value = "权限集合", notes = "菜单和细分权限", required = true)
    private List<String> permissionIds;

    @NotNull(message = "菜单集合参数为必填")
    @ApiModelProperty(value = "菜单集合", notes = "菜单权限", required = true)
    private List<String> menuIds;

}

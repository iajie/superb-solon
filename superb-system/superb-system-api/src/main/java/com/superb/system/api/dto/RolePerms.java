package com.superb.system.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-02 16:17
 */
@Data
@ApiModel(value = "角色权限")
public class RolePerms {

    @ApiModelProperty(value = "角色菜单id")
    private List<String> menuIds;

    @ApiModelProperty(value = "角色权限id")
    private List<String> permissionIds;

}

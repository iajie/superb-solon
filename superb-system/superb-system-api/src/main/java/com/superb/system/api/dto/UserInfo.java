package com.superb.system.api.dto;

import com.superb.system.api.entity.SystemPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-27 12:56
 */
@Data
@ApiModel(value = "用户信息")
public class UserInfo {

    @ApiModelProperty(value = "用户信息")
    private User user;

    @ApiModelProperty(value = "权限集合")
    private List<String> permissions;

    @ApiModelProperty(value = "角色集合")
    private List<String> roles;

    @ApiModelProperty(value = "数据权限集合")
    private List<DataScope> dataScopes;

    @ApiModelProperty(value = "顶部菜单集合")
    private List<SystemPermission> topMenu;

    @ApiModelProperty(value = "侧边栏菜单集合")
    private List<SystemPermission> leftMenu;
}

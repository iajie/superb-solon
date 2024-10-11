package com.superb.system.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;

/**
 * 系统菜单权限表
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Data
@NoArgsConstructor
@Table(value = "system_permission")
@ApiModel(value = "系统菜单权限表")
@EqualsAndHashCode(callSuper=false)
public class SystemPermission extends BaseEntity {

    @NotNull(message = "菜单类型不能为空")
    @ApiModelProperty(value = "菜单类型", notes = "0系统菜单；1顶部菜单；2中台菜单", required = true)
    private Integer type;

    @NotNull(message = "菜单属性不能为空")
    @ApiModelProperty(value = "菜单属性", notes = "0父级菜单；1子菜单；2权限", required = true)
    private Integer menuType;

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty(value = "名称", notes = "菜单和权限名称", required = true)
    private String name;

    @ApiModelProperty(value = "菜单路由")
    private String path;

    @ApiModelProperty(value = "组件名称")
    private String component;

    @ApiModelProperty(value = "权限代码")
    private String perms;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "父级菜单id")
    private String parentId;

    @ApiModelProperty(value = "外链", notes = "0否；1是")
    private Integer outerChain;

    @ApiModelProperty(value = "状态", notes = "0启用；1禁用")
    private Integer status;

    @NotNull(message = "排序不能为空")
    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    @ApiModelProperty(value = "子菜单")
    @Column(ignore = true)
    private List<SystemPermission> children;

    @ApiModelProperty(value = "权限集合", hidden = true)
    @Column(ignore = true)
    private List<SystemPermission> permissions;

    /**
     * 权限和菜单不存在部门-需要分配关联
     */
    @ApiModelProperty(hidden = true)
    @Column(ignore = true)
    private String organId;

    public SystemPermission(String id, Integer del) {
        super(id, del);
    }
}
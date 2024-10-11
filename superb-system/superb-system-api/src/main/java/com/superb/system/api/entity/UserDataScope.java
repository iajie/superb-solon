package com.superb.system.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.superb.common.database.entity.BaseEntity;
import com.superb.system.api.dto.Options;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.List;

/**
 * 用户数据权限范围
 * @Author: ajie
 * @CreateTime: 2024-7-3
 */
@Data
@NoArgsConstructor
@Table(value = "system_user_data_scope")
@ApiModel(value = "用户数据权限范围")
@EqualsAndHashCode(callSuper=false)
public class UserDataScope extends BaseEntity {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    @ApiModelProperty(value = "角色id", notes = "用户角色，最多可选择10个角色")
    private String roleId;

    @ApiModelProperty(value = "数据权限范围", notes = "0本人；1全部；2本部门；3本部门及子部门；4自定义；5子部门")
    private Integer dataScopeType;

    @ApiModelProperty(value = "是否默认", notes = "0否；1是")
    private Integer isMain;

    @ApiModelProperty(value = "是否启用", notes = "0否；1是")
    private Integer enable;

    @ApiModelProperty(value = "自定义部门权限", notes = "部门选择最多20个部门")
    private String dataScopeOrganId;

    @ApiModelProperty(value = "自定义部门权限范围", notes = "0本部门；1本部门及子部门")
    private Integer dataScopeOrganType;

    @Column(ignore = true)
    @ApiModelProperty(value = "角色列表")
    private List<Options> roles;

    @Column(ignore = true)
    @ApiModelProperty(value = "角色id列表")
    private List<String> roleIds;

    @Column(ignore = true)
    @ApiModelProperty(value = "自定义部门列表")
    private List<Options> organs;
}
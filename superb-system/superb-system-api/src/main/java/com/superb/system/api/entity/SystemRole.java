package com.superb.system.api.entity;

import com.mybatisflex.annotation.Table;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 系统角色表
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Data
@NoArgsConstructor
@Table(value = "system_role")
@ApiModel(value = "系统角色表")
@EqualsAndHashCode(callSuper=false)
public class SystemRole extends BaseEntity {

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称", required = true)
    private String name;

    @NotBlank(message = "角色代码不能为空")
    @ApiModelProperty(value = "角色代码", required = true)
    private String code;

    @ApiModelProperty(value = "状态", notes = "0启用；1禁用")
    private Integer status;

    @NotNull(message = "排序不能为空")
    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    @NotNull(message = "角色类型不能为空")
    @ApiModelProperty(value = "角色类型", notes = "0系统角色1部门角色", required = true)
    private Integer type;

    public SystemRole(String id, Integer del) {
        super(id, del);
    }
}
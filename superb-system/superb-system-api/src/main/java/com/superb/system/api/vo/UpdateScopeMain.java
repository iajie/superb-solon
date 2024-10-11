package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-09 08:49
 */
@Data
@ApiModel(value = "修改是否为默认部门")
public class UpdateScopeMain {

    @NotBlank(message = "数据权限id不能为空")
    @ApiModelProperty(value = "数据权限id", required = true)
    private String id;

    @NotNull(message = "是否默认参数不能为空")
    @ApiModelProperty(value = "是否默认", required = true)
    private Integer isMain;

}

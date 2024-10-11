package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-03 14:56
 */
@Data
@ApiModel(value = "更新账号信息")
public class UpdateAccount {

    @ApiModelProperty(value = "用户名")
    @NotNull(message = "用户名不能为空", groups = Username.class)
    private String username;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @NotNull(message = "新密码不能为空", groups = Password.class)
    @ApiModelProperty(value = "新密码")
    private String newPassword;

    public interface Username {}

    public interface Password {}
}

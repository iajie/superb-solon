package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-14 09:30
 */
@Data
@ApiModel(value = "账号密码登录")
public class PwdLogin {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "登录密码不能为空")
    @ApiModelProperty(value = "登录密码", required = true)
    private String password;

    @NotBlank(message = "登录验证码不能为空")
    @ApiModelProperty(value = "验证码", required = true)
    private String code;

    @NotBlank(message = "验证码秘钥不能为空")
    @ApiModelProperty(value = "生成验证码的key", required = true)
    private String key;

    @ApiModelProperty(value = "登录类型：0用户名登录、1手机号")
    private Integer type = 0;

}

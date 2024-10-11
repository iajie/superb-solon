package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-14 09:30
 */
@Data
@ApiModel(value = "手机号验证码登录")
public class PhoneCodeLogin {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;

}

package com.superb.system.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-24 13:49
 */
@Data
@ApiModel(value = "验证码")
public class ValidateImage {

    @ApiModelProperty(value = "秘钥", notes = "登录时需要使用该秘钥配合校验")
    private String key;

    @ApiModelProperty(value = "验证码", notes = "base64data字符串")
    private String data;

}

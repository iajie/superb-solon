package com.superb.system.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-14 15:23
 */
@Data
@AllArgsConstructor
@ApiModel(value = "登录返回信息")
public class Token {

    @ApiModelProperty(value = "请求头", notes = "其他接口请求时需要在headers中添加该参数")
    private String tokenName;

    @ApiModelProperty(value = "登录凭证", notes = "与tokenName形成键值对，访问其他接口")
    private String token;

}

package com.superb.system.api.dto;

import com.superb.common.core.enums.DeviceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-23 11:26
 */
@Data
@ApiModel(value = "在线用户")
public class UserOnLine {

    @ApiModelProperty(value = "登录凭证")
    private String token;

    @ApiModelProperty(value = "登录客户端")
    private String deviceType;

    @ApiModelProperty(value = "数据权限部门")
    private String organId;

    @ApiModelProperty(value = "登录ip")
    private String loginIp;

    @ApiModelProperty(value = "登录方式：0账号密码登录；1手机号登录；2手机验证码登录；3扫码登录；4第三方登录")
    private Integer loginType;

    @ApiModelProperty(value = "登录时间")
    private String loginTime;

    @ApiModelProperty(value = "最后活跃时间")
    private String lastActiveTime;

}

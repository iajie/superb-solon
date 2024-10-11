package com.superb.system.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.Pattern;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-03 11:03
 */
@Data
@ApiModel(value = "修改用户基础信息")
public class UpdateCurrent {

    @ApiModelProperty(value = "头像")
    private String avatar;

    @Email
    @ApiModelProperty(value = "邮箱")
    private String email;

    @Pattern(value = "^1[3-9]\\d{9}$", message = "电话号码不正确")
    @ApiModelProperty(value = "电话号码")
    private String phoneNumber;

    @ApiModelProperty(value = "身份证号")
    @Pattern(value = "(\\d{15}$ )|(^\\d{18}$ )|(\\d{17}(\\d|X|x)$)", message = "身份证号不正确")
    private String idcard;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "备注", notes = "拓展信息")
    private String remarks;

}

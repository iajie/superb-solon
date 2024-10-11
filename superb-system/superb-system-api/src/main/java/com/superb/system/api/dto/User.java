package com.superb.system.api.dto;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-30 16:02
 */
@Data
public class User {

    @ApiModelProperty(value = "用户名", notes = "用于登录的名称")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "电话号码")
    private String phoneNumber;

    @ApiModelProperty(value = "电子邮箱")
    private String email;

    @ApiModelProperty(value = "身份证号")
    private String idcard;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "性别", notes = "0男；1女")
    private Integer sex;

    @ApiModelProperty(value = "是否认证", notes = "0否；1是(是否实名)")
    private Integer authentication;

    @ApiModelProperty(value = "状态", notes = "0启用；1禁用")
    private Integer status;

    @ApiModelProperty(value = "租户管理员", notes = "0否；1是")
    private Integer superb;

    @ApiModelProperty(value = "所属部门")
    private String organId;

    @ApiModelProperty(value = "备注", notes = "信息拓展json")
    private JSONObject remarks;

    public void setRemarks(String remarks) {
        this.remarks = JSON.parseObject(remarks);
    }
}

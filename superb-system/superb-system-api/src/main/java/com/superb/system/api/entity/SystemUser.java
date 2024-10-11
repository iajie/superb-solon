package com.superb.system.api.entity;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.mask.Masks;
import com.superb.common.database.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 系统用户表
 * @Author: ajie
 * @CreateTime: 2024-5-7
 */
@Data
@NoArgsConstructor
@ApiModel(value = "系统用户表")
@Table(value = "system_user")
@EqualsAndHashCode(callSuper=false)
public class SystemUser extends BaseEntity {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名：用于登录的名称", required = true)
    private String username;

    @ApiModelProperty(value = "登录密码", required = true)
    private String password;

    @ApiModelProperty(value = "用户随机盐")
    private String salt;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "电话号码")
    @ColumnMask(Masks.FIXED_PHONE)
    private String phoneNumber;

    @ApiModelProperty(value = "电子邮箱")
    @ColumnMask(Masks.EMAIL)
    private String email;

    @ApiModelProperty(value = "身份证号")
    @ColumnMask(Masks.ID_CARD_NUMBER)
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

    public SystemUser(String id, Integer del) {
        super(id, del);
    }
}
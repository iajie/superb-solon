package com.superb.system.api.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.mask.Masks;
import com.superb.common.database.entity.BaseEntity;
import com.superb.common.database.listener.SuperbInsertListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 系统租户管理表
 * @Author: ajie
 * @CreateTime: 2024-5-11
 */
@Data
@NoArgsConstructor
@Table(value = "system_tenant")
@ApiModel(value = "系统租户管理表")
@EqualsAndHashCode(callSuper=false)
public class SystemTenant extends BaseEntity {

    @NotBlank(message = "租户id标识不能为空")
    @ApiModelProperty(value = "租户id标识", required = true)
    private String tenantKey;

    @NotBlank(message = "租户名称不能为空")
    @ApiModelProperty(value = "租户名称", required = true)
    private String name;

    @ApiModelProperty(value = "租户logo")
    private String logo;

    @ApiModelProperty(value = "法人")
    private String legalPerson;

    @ApiModelProperty(value = "联系电话")
    @ColumnMask(Masks.FIXED_PHONE)
    private String phone;

    @ApiModelProperty(value = "身份证号")
    @ColumnMask(Masks.ID_CARD_NUMBER)
    private String idcard;

    @ApiModelProperty(value = "银行卡号")
    @ColumnMask(Masks.BANK_CARD_NUMBER)
    private String bankNo;

    @ApiModelProperty(value = "开户行")
    private String bank;

    @ApiModelProperty(value = "统一信用代码")
    @ColumnMask(Masks.ID_CARD_NUMBER)
    private String creditCode;

    @ApiModelProperty(value = "企业注册地址")
    @ColumnMask(Masks.ADDRESS)
    private String registeredAddress;

    @ApiModelProperty(value = "营业执照")
    private String businessImage;

    @ApiModelProperty(value = "身份证正面")
    private String frontIdcard;

    @ApiModelProperty(value = "身份证反面")
    private String backIdcard;

    @ApiModelProperty(value = "租户状态", notes = "0启用；1禁用")
    private Integer status;

    @ApiModelProperty(value = "租户页脚配置")
    private String footer;

    @ApiModelProperty(hidden = true)
    @Column(ignore = true)
    private String tenantId;

    @ApiModelProperty(hidden = true)
    @Column(ignore = true)
    private String organId;

    public SystemTenant(String id, Integer del) {
        super(id, del);
    }

}
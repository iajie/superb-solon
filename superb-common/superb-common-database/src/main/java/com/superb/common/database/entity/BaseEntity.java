package com.superb.common.database.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-07 10:36
 */
@Data
@NoArgsConstructor
public class BaseEntity implements Serializable {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    @ApiModelProperty(value = "业务主键", hidden = true)
    private String id;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    @Column(ignore = true)
    @ApiModelProperty(value = "创建人")
    private String createName;

    @ApiModelProperty(value = "租户id", hidden = true)
    private String tenantId;

    @ApiModelProperty(value = "部门id", hidden = true)
    private String organId;

    @Column(ignore = true)
    @ApiModelProperty(value = "部门名称")
    private String organName;

    @Column(isLogicDelete = true)
    @ApiModelProperty(value = "删除标识", notes = "0否；1是", hidden = true)
    private Integer del;

    @ApiModelProperty(value = "备注")
    private String remarks;

    public BaseEntity(String id, Integer del) {
        this.id = id;
        this.del = del;
    }
}

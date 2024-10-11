package com.superb.common.security.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-15 15:57
 */
@Data
public class SuperbTenant {

    private String tenantKey;

    private String name;

    private String logo;

    private String legalPerson;

    private String phone;

    private String idcard;

    private String bankNo;

    private String bank;

    private String creditCode;

    private String registeredAddress;

    private String businessImage;

    private String frontIdcard;

    private String backIdcard;

    private Date createTime;

    private String createBy;

    private Integer del;

    private Integer status;

}

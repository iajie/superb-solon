package com.superb.common.security.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-15 15:54
 */
@Data
public class SuperbUser {

    private String id;

    private String username;

    private String password;

    private String salt;

    private String nickname;

    private String phoneNumber;

    private String email;

    private String idcard;

    private String avatar;

    private Integer sex;

    private Integer authentication;

    private Integer status;

    private Integer superb;

    private Date createTime;

    private String createBy;

    private String tenantId;

    private List<String> permissions;

    private List<String> roles;
}

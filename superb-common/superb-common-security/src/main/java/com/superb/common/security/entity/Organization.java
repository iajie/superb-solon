package com.superb.common.security.entity;

import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-14 13:47
 */
@Data
public class Organization {

    private String id;

    private Integer parentCode;

    private String customCode;

    private String name;

    private String shortName;

    private String areaCode;

    private String areaName;

    private Integer sort;

    private Double longitude;

    private Double latitude;

    private String address;

    private Integer type;

}

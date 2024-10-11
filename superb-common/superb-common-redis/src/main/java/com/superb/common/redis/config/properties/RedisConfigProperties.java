package com.superb.common.redis.config.properties;

import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-26 17:15
 */
@Data
public class RedisConfigProperties {

    private String address;

    private Integer database;

    private String password;

}

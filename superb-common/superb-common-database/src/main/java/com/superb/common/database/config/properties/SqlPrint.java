package com.superb.common.database.config.properties;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-31 13:17
 */
@Data
public class SqlPrint {

    /**
     * 不打印的表
     */
    private Set<String> ignoreTable = new HashSet<>();

    /**
     * 控制台打印
     */
    private String console = "\nSQL:{}, 耗时:【{}ms】";

}

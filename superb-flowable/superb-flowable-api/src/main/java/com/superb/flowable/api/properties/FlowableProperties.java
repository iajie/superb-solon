package com.superb.flowable.api.properties;

import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-09 14:51
 */
@Data
public class FlowableProperties {

    /**
     * 数据库表操作
     * 在构建流程引擎后，会执行检查
     */
    private String databaseSchemaUpdate;

    private String asyncExecutorActivate;

}

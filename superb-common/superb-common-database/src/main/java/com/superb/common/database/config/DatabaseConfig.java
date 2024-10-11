package com.superb.common.database.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.dynamicds.DynamicDataSource;

import javax.sql.DataSource;

/**
 * 数据源获取
 * @Author: ajie
 * @CreateTime: 2024-07-26 11:23
 */
@Configuration
public class DatabaseConfig {

    /**
     * 动态数据源
     * @param dataSource
     * typed 表示默认数据源
     * @return
     */
    @Bean(value = "superbDataSource", typed = true)
    public DataSource superbDataSource(@Inject(value = "${solon.superbDataSource}", autoRefreshed = true) DynamicDataSource dataSource) {
        return dataSource;
    }

}

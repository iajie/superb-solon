package com.superb.common.database;

import com.superb.common.database.annotation.handler.DataScopeRouteInterceptor;
import com.superb.common.database.config.DatabaseConfig;
import com.superb.common.database.config.MybatisFlexConfig;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 注册数据源和flex配置插件
 *
 * @Author: ajie
 * @CreateTime: 2024-07-26 11:05
 */
public class DatabasePlugin implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        context.beanMake(DatabaseConfig.class);
        context.beanMake(MybatisFlexConfig.class);
        context.beanMake(DataScopeRouteInterceptor.class);
    }
}

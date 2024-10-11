package com.superb.common.database.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.superb.common.database.config.properties.SqlPrint;
import com.superb.common.database.config.properties.TableProperties;
import com.superb.common.database.entity.BaseEntity;
import com.superb.common.database.listener.SuperbInsertListener;
import com.superb.common.database.service.SuperbCommonsDialectImpl;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-26 11:28
 */
@Slf4j
@Configuration
public class MybatisFlexConfig {

    @Bean
    public void superbGlobalConfig(TableProperties properties) {
        FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();
        // 注册新增填充
        globalConfig.registerInsertListener(new SuperbInsertListener(), BaseEntity.class);
        // 不打印banner
        globalConfig.setPrintBanner(false);
        // 注册sql条件追加器
        DialectFactory.registerDialect(DbType.MYSQL, new SuperbCommonsDialectImpl(properties));
    }

    /**
     * SQL审计，打印自定义
     * 可排除打印表
     * @param print
     */
    @Bean
    public void superbSqlLog(SqlPrint print) {
        // 开启审计功能（SQL打印分析的功能是使用 SQL审计模块完成的）
        AuditManager.setAuditEnable(true);
        // 设置 SQL审计收集器
        AuditManager.setMessageCollector(auditMessage -> {
            if (print.getIgnoreTable().stream().noneMatch(i -> auditMessage.getFullSql().contains(i))) {
                log.info("\n" + print.getConsole(), auditMessage.getFullSql(), auditMessage.getElapsedTime());
            }
        });
    }

    @Bean
    public TableProperties tableProperties(@Inject(value = "${superb.custom}", autoRefreshed = true) TableProperties properties) {
        return properties;
    }

    @Bean
    public SqlPrint sqlPrint(@Inject(value = "${solon.sql.print}", autoRefreshed = true) SqlPrint print) {
        return print;
    }

}

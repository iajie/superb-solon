package com.superb.flowable.config;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.image.ProcessDiagramGenerator;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-09 14:39
 */
@Configuration
public class FlowableConfig {


    /**
     * 初始化流程引擎
     * @return
     */
    @Bean
    public ProcessEngine engineConfiguration(DataSource dataSource) {
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        // 设置数据源
        configuration.setDataSource(dataSource);
        // true 会对数据库中所有表进行更新操作。如果表不存在，则自动创建(建议开发时使用)
        configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 定时任务JOB 是否开启
        configuration.setAsyncExecutorActivate(false);
        //             发送邮箱配置-可配置-跟随租户
        // 发送邮箱端口
        // configuration.setMailServerPort(465);
        // 发送邮件地址
        // configuration.setMailServerHost("smtp.qq.com");
//        configuration.setMailServerUsername();
//        configuration.setMailServerPassword();
        return configuration.buildProcessEngine();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public ProcessDiagramGenerator processDiagramGenerator(ProcessEngine processEngine) {
        return processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
    }
}

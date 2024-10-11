package com.superb.common.core.change;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.CloudClient;

/**
 * 获取服务节点变化和配置变化
 * https://solon.noear.org/article/76
 * @Author: ajie
 * @CreateTime: 2024-08-01 10:44
 */
@Slf4j
@Configuration
public class ServiceDiscovery {

    @Bean
    public void serviceChange() {
        String group = Solon.cfg().appGroup();
        String service = Solon.cfg().appName();
        String env = Solon.cfg().env();
        CloudClient.discovery().attention(group, service, d -> log.debug("\n服务[{}]变更：{}", service, d.cluster()));
        CloudClient.config().attention("DEFAULT_GROUP", "application-" + env + ".yml", d -> log.debug("\n公共配置变化: {}", d.value()));
        CloudClient.config().attention("DEFAULT_GROUP", service + "-" + env + ".yml", d -> log.debug("\n配置[{}]变化: {}", service, d.value()));
    }

}

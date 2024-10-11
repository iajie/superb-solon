package com.superb.gateway.config;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.rx.Completable;

/**
 * package: com.superb.gateway.config
 * date 2024-09-02-9:24
 * description: 网关过滤器配置
 *
 * @author MoJie
 */
@Configuration
public class SuperbCloudGatewayFilter implements CloudGatewayFilter {


    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        return null;
    }
}

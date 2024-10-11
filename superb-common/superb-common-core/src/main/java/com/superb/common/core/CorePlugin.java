package com.superb.common.core;

import com.superb.common.core.change.ServiceDiscovery;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 核心包
 * @Author: ajie
 * @CreateTime: 2024-08-01 11:02
 */
public class CorePlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        // 注册服务注册发现-配置变更打印
        context.beanMake(ServiceDiscovery.class);
    }
}

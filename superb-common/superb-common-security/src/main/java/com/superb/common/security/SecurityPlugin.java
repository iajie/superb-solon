package com.superb.common.security;

import com.superb.common.security.configure.TokenConfigure;
import com.superb.common.security.filter.GlobalFilter;
import com.superb.common.security.service.NoRepeatSubmitCheckerImpl;
import com.superb.common.security.service.StpInterfaceImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 注册鉴权插件
 *
 * @Author: ajie
 * @CreateTime: 2024-07-25 17:48
 */
public class SecurityPlugin implements Plugin {

    /**
     * 注册bean
     *
     * @param context 应用上下文
     * @throws Throwable
     */
    @Override
    public void start(AppContext context) throws Throwable {
        context.beanMake(TokenConfigure.class);
        context.beanMake(StpInterfaceImpl.class);
        context.beanMake(NoRepeatSubmitCheckerImpl.class);
//        context.beanMake(AuthProcessorImpl.class);
        context.beanMake(GlobalFilter.class);
    }
}

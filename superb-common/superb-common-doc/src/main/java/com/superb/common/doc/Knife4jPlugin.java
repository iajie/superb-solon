package com.superb.common.doc;

import com.superb.common.doc.config.Knife4jConfig;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-25 15:34
 */
public class Knife4jPlugin implements Plugin {

    /**
     * 注册bean
     * @param context 应用上下文
     * @throws Throwable
     */
    @Override
    public void start(AppContext context) throws Throwable {
        context.beanMake(Knife4jConfig.class);
    }

}

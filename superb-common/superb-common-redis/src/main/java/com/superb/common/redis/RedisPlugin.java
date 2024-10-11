package com.superb.common.redis;

import com.superb.common.redis.annotation.SuperbLockHandler;
import com.superb.common.redis.config.RedissonConfig;
import com.superb.common.redis.token.SaTokenCacheImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-25 17:00
 */
public class RedisPlugin implements Plugin {

    /**
     * 注册redisson
     * @param context 应用上下文
     * @throws Throwable
     */
    @Override
    public void start(AppContext context) throws Throwable {
        // 注册sa-token信息存储逻辑
        context.beanMake(SaTokenCacheImpl.class);
        // 注册redisson
        context.beanMake(RedissonConfig.class);
    }
}

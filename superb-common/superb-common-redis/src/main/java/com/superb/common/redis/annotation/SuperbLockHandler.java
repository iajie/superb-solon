package com.superb.common.redis.annotation;

import com.superb.common.redis.key.RedisLockKey;
import com.superb.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

import java.time.Duration;

/**
 * redisson 分布式方法锁注解实现
 * @Author: ajie
 * @CreateTime: 2024-07-24 10:28
 */
@Slf4j
public class SuperbLockHandler implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        SuperbLock annotation = inv.getMethodAnnotation(SuperbLock.class);
        if (annotation == null) {
            return inv.invoke();
        }
        log.debug("[开始]执行RedisLock环绕通知,获取Redis分布式锁开始");
        RedisLockKey lockKey = new RedisLockKey(annotation.value(), Duration.of(annotation.leaseTime(), annotation.timeUnit()));
        // 方法执行前加锁
        RedisUtils.build().lock().lock(lockKey);
        // 执行代码
        Object result = inv.invoke();
        // 释放锁
        RedisUtils.build().lock().unlock(lockKey);
        log.debug("释放Redis分布式锁[成功]，解锁完成，结束业务逻辑...");
        return result;
    }
}

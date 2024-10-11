package com.superb.common.security.service;

import com.superb.common.redis.config.RedissonConfig;
import com.superb.common.redis.key.RedisLockKey;
import com.superb.common.redis.utils.RedisUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.NoRepeatSubmit;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;

import java.time.Duration;

/**
 * 防重复提交
 * @Author: ajie
 * @CreateTime: 2024-07-26 09:01
 */
@Component
@Condition(onClass = RedissonConfig.class)
public class NoRepeatSubmitCheckerImpl implements NoRepeatSubmitChecker {

    /**
     * 针对于 NoRepeatSubmit默认使用本地延时锁，定制为分布式锁
     * @param anno  防重复提交注解
     * @param ctx  请求上下文
     * @param submitHash  是否是重复提交的判断标准：根据注解中的 value()的类型（3种：请求头、请求参数、请求体）取值，将值使用 MD5消息摘要算法计算得出 submitHash值
     * @param limitSeconds  间隔时间（作为锁的释放时间）
     * @return  是否获取到了锁。false代表没有获取到锁，说明锁被 “上一次的相同请求” 抢了，即这一次请求是重复提交的请求，将抛出 ValidatorException 异常（请勿重复提交）
     */
    @Override
    public boolean check(NoRepeatSubmit anno, Context ctx, String submitHash, int limitSeconds) {
        RedisLockKey lockKey = new RedisLockKey(submitHash, Duration.ofSeconds(limitSeconds), Duration.ZERO);
        return RedisUtils.build().lock().tryLock(lockKey);
    }
}

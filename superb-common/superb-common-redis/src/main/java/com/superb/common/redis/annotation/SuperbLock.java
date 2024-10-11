package com.superb.common.redis.annotation;

import org.noear.solon.annotation.Around;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

/**
 * 分布式锁注解
 * @Author: ajie
 * @CreateTime: 2024-07-24 10:23
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Around(value = SuperbLockHandler.class)
public @interface SuperbLock {

    /**
     * 锁名称
     * @return
     */
    String value();

    /**
     * 锁的有效时间
     */
    int leaseTime() default 30;

    /**
     * 时间格式，默认为妙
     * @return
     */
    ChronoUnit timeUnit() default ChronoUnit.SECONDS;
}

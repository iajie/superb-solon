package com.superb.common.redis.command;

import com.superb.common.redis.key.RedisLockKey;

/**
 * 基于redisson实现的锁
 * @Author: ajie
 * @CreateTime: 2024-07-24 09:14
 */
public interface RedisLockCommands {

    /**
     * 加锁操作
     * @param redisKey 设置锁、过期时间
     */
    void lock(RedisLockKey redisKey);

    /**
     * 加锁操作(tryLock锁）
     * @param redisKey 存在等待时间
     * @return
     */
    boolean tryLock(RedisLockKey redisKey);

    /**
     * 释放锁
     * @param redisKey
     * @return
     */
    void unlock(RedisLockKey redisKey);

    /**
     * 判断当前线程是否持有锁，会出现异常
     * @param redisKey
     * @return
     */
    boolean isLock(RedisLockKey redisKey);

    /**
     * 判断当前线程是否持有锁，不会出现异常
     * @param redisKey
     * @return
     */
    boolean isHeldByCurrentThread(RedisLockKey redisKey);
}

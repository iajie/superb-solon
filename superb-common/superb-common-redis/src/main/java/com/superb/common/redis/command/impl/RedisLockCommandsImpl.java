package com.superb.common.redis.command.impl;

import com.superb.common.redis.command.RedisLockCommands;
import com.superb.common.redis.key.RedisLockKey;
import com.superb.common.redis.utils.AbstractSuperbRedis;
import com.superb.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-24 10:09
 */
@Slf4j
public class RedisLockCommandsImpl extends AbstractSuperbRedis implements RedisLockCommands {
    public RedisLockCommandsImpl(RedisUtils redisUtils) {
        super(redisUtils);
    }

    @Override
    public void lock(RedisLockKey redisKey) {
        RLock lock = this.redisClient.getLock(redisKey.getKey());
        if (redisKey.getDuration() != null) {
            // 存在自定义锁时间
            lock.lock(redisKey.getDuration().toSeconds(), TimeUnit.SECONDS);
        } else {
            // 没有自定义时间，则默认30s
            lock.lock();
        }
    }

    @Override
    public boolean tryLock(RedisLockKey redisKey) {
        RLock lock = this.redisClient.getLock(redisKey.getKey());
        boolean getLock = false;
        try {
            // 是否存在等待锁时间
            if (redisKey.getWaitTime() != null) {
                getLock = lock.tryLock(redisKey.getWaitTime().toSeconds(), redisKey.getDuration().toSeconds(), TimeUnit.SECONDS);
            } else {
                getLock = lock.tryLock(redisKey.getDuration().toSeconds(), TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.error("获取Redisson分布式锁[异常]，lockName={}", redisKey.getKey(), e);
        }
        return getLock;
    }

    @Override
    public void unlock(RedisLockKey redisKey) {
        //如果该线程还持有该锁，那么释放该锁。如果该线程不持有该锁，说明该线程的锁已到过期时间，自动释放锁 解决空白出现异常的情况
        if (isHeldByCurrentThread(redisKey)) {
            this.redisClient.getLock(redisKey.getKey()).unlock();
        }
    }

    @Override
    public boolean isLock(RedisLockKey redisKey) {
        return this.redisClient.getLock(redisKey.getKey()).isLocked();
    }

    @Override
    public boolean isHeldByCurrentThread(RedisLockKey redisKey) {
        return this.redisClient.getLock(redisKey.getKey()).isHeldByCurrentThread();
    }
}

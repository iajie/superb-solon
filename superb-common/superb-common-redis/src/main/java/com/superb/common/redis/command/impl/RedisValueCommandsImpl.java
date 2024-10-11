package com.superb.common.redis.command.impl;

import com.superb.common.redis.command.RedisValueCommands;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.key.RedisLockKey;
import com.superb.common.redis.utils.AbstractSuperbRedis;
import com.superb.common.redis.utils.RedisUtils;
import lombok.SneakyThrows;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-21 09:02
 */
public class RedisValueCommandsImpl extends AbstractSuperbRedis implements RedisValueCommands {

    public RedisValueCommandsImpl(RedisUtils redisUtils) {
        super(redisUtils);
    }

    @Override
    public <T> Boolean set(RedisKey key, T value) {
        try {
            RBatch batch = this.redisClient.createBatch();
            RBucketAsync<T> bucket = batch.getBucket(key.getKey());
            bucket.setAsync(value);
            if (key.getDuration() != null && key.getDuration().toSeconds() != 0) {
                bucket.expireAsync(key.getDuration());
            }
            batch.execute();
            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    @Override
    public <T> T get(RedisKey key) {
        RBucket<T> bucket = this.redisClient.getBucket(key.getKey());
        return bucket.get();
    }

    /**
     * 获取缓存，如果缓存不存在则存入
     * @param key 存入key
     * @param data 存入数据
     * @param get 返回结果
     * @return
     * @param <R>
     * @param <T>
     */
    @Override
    @SneakyThrows
    public <R, T> T getNullSet(RedisKey key, Supplier<R> data, Function<RedisUtils, T> get) {
        RedisLockKey lockKey = new RedisLockKey(key.getRedisKey());
        if (this.redisUtils.lock().tryLock(lockKey)) {
            try {
                // 不存在该值，则存入缓存
                if (!this.exists(key)) {
                    R r = data.get();
                    if (r != null) {
                        this.set(key, r);
                    }
                }
            } finally {
                // 释放锁
                this.redisUtils.lock().unlock(lockKey);
            }
        }
        return get.apply(this.redisUtils);
    }
}

package com.superb.common.redis.key;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-24 09:17
 */
public class RedisLockKey extends RedisKey {

    @Getter
    @Setter
    private Duration waitTime;

    /**
     * 设置锁
     * @param key
     * @param duration 锁有效时间
     */
    public RedisLockKey(String key, Duration duration) {
        super(KeyType.LOCK, duration, key);
    }

    /**
     * 设置锁
     * @param key
     * @param duration 锁有效时间
     * @param waitTime 等待时间
     */
    public RedisLockKey(String key, Duration duration, Duration waitTime) {
        super(KeyType.LOCK, duration, key);
        this.waitTime = waitTime;
    }

    /**
     * 设置锁
     * @param key
     */
    public RedisLockKey(String key) {
        super(KeyType.LOCK, Duration.ofSeconds(30), key);
    }

}

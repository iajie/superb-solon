package com.superb.common.redis.utils;

import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.redis.command.RedisHashCommands;
import com.superb.common.redis.command.RedisListCommands;
import com.superb.common.redis.command.RedisLockCommands;
import com.superb.common.redis.command.RedisValueCommands;
import com.superb.common.redis.command.impl.RedisHashCommandsImpl;
import com.superb.common.redis.command.impl.RedisListCommandsImpl;
import com.superb.common.redis.command.impl.RedisLockCommandsImpl;
import com.superb.common.redis.command.impl.RedisValueCommandsImpl;
import org.noear.solon.Solon;
import org.redisson.api.RedissonClient;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-17 12:55
 */
public class RedisUtils extends AbstractSuperbRedis {

    public RedisUtils(RedissonClient redisClient) {
        super(redisClient);
    }

    public static RedisUtilsBuilder builder() {
        return new RedisUtilsBuilder();
    }

    public static RedisUtils build() {
        return new RedisUtilsBuilder().build();
    }
    public static RedisUtils build(Integer index) {
        return new RedisUtilsBuilder().build(index);
    }

    public RedissonClient getRedisClient() {
        return this.redisClient;
    }

    public RedisHashCommands hash() {
        return new RedisHashCommandsImpl(this);
    }

    public RedisListCommands list() {
        return new RedisListCommandsImpl(this);
    }

    public RedisLockCommands lock() {
        return new RedisLockCommandsImpl(this);
    }

    public RedisValueCommands value() {
        return new RedisValueCommandsImpl(this);
    }

    /**
     * 删除所有符合给定模式 pattern 的 key
     *
     * @param pattern
     * @return
     */
    public void delKeys(final String pattern) {
        this.redisClient.getKeys().deleteByPattern(this.getNameMapper().map(pattern));
    }
    public static class RedisUtilsBuilder {
        private static final int MIX = 0;
        private static final int MAX = 15;

        private RedissonClient redisClient;

        /**
         * 设置redis连接，默认存到0库
         * @return
         */
        public RedisUtils build() {
            return build(0);
        }

        /**
         * 选择存入缓存到哪个库
         * 不稳定，导致登录状态失效。。。先保护，更新好后开放
         * @param databaseIndex redis库索引
         * @return
         */
        protected RedisUtils build(Integer databaseIndex) {
            if (databaseIndex < MIX || databaseIndex > MAX) {
                throw new SuperbException(SuperbCode.REDIS_DATABASE, "redis database index超出指定范围(0-15)" + databaseIndex);
            }
            try {
                this.redisClient = Solon.context().getBean(RedissonClient.class);
            } catch (Exception e) {
                throw new SuperbException(SuperbCode.BEAN_CREATE);
            }
            return new RedisUtils(this.redisClient);
        }
    }

}

package com.superb.common.redis.utils;

import com.superb.common.redis.key.RedisKey;
import org.redisson.api.NameMapper;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * redis模板抽象实现
 *
 * @Author: ajie
 * @CreateTime: 2024-05-17 14:17
 */
public abstract class AbstractSuperbRedis {

    protected RedisUtils redisUtils;
    protected RedissonClient redisClient;

    public AbstractSuperbRedis(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
        this.redisClient = redisUtils.getRedisClient();
    }

    public AbstractSuperbRedis(RedissonClient redisClient) {
        this.redisClient = redisClient;
    }

    public NameMapper getNameMapper() {
        Config config = this.redisClient.getConfig();
        if (config.isClusterConfig()) {
            return config.useClusterServers().getNameMapper();
        }
        return config.useSingleServer().getNameMapper();
    }

    /**
     * redis续命 当 key 过期时(生存时间为 0 )，它会被自动删除
     *
     * @param key
     * @return
     */
    public boolean expire(RedisKey key) {
        return this.redisClient.getBucket(key.getKey()).expire(key.getDuration());
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     * 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1
     *
     * @param key
     * @return
     */
    public Long ttl(RedisKey key) {
        return this.redisClient.getBucket(key.getKey()).remainTimeToLive();
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(RedisKey key) {
        RKeys keys = this.redisClient.getKeys();
        return keys.countExists(key.getKey()) > 0;
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key
     * @return
     */
    public boolean del(RedisKey ...key) {
        String keyStrs = Arrays.stream(key).map(RedisKey::getKey).collect(Collectors.joining(","));
        return this.redisClient.getKeys().delete(keyStrs.split(",")) > 0;
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key
     * @return
     */
    public boolean dels(RedisKey key) {
        Collection<String> keys = this.keys(key.getKey());
        return this.redisClient.getKeys().delete(String.join(",", keys).split(",")) > 0;
    }

    public Collection<String> keys(final String pattern) {
        Stream<String> stream = this.redisClient.getKeys().getKeysStreamByPattern(getNameMapper().map(pattern));
        return stream.map(key -> getNameMapper().unmap(key)).collect(Collectors.toList());
    }

}

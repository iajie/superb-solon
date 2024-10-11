package com.superb.common.redis.command.impl;

import com.superb.common.redis.command.RedisListCommands;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.AbstractSuperbRedis;
import com.superb.common.redis.utils.RedisUtils;
import org.redisson.api.RList;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-19 10:21
 */
public class RedisListCommandsImpl extends AbstractSuperbRedis implements RedisListCommands {
    public RedisListCommandsImpl(RedisUtils redisUtils) {
        super(redisUtils);
    }

    @Override
    public <T> boolean set(RedisKey key, List<T> dataList) {
        RList<T> list = this.redisClient.getList(key.getKey());
        return list.addAll(dataList);
    }

    @Override
    public <T> List<T> get(RedisKey key) {
        RList<T> list = this.redisClient.getList(key.getKey());
        return list.readAll();
    }

    @Override
    public <T> List<T> listPage(RedisKey key, int start, int end) {
        RList<T> list = this.redisClient.getList(key.getKey());
        return list.range(start, end);
    }

    @Override
    public <T> long listSize(RedisKey key) {
        RList<T> list = this.redisClient.getList(key.getKey());
        return list.size();
    }

    @Override
    public <T> void update(RedisKey key, int index, T value) {
        RList<T> list = this.redisClient.getList(key.getKey());
        list.set(index, value);
    }

    @Override
    public <T> void remove(RedisKey key, T value) {
        RList<T> list = this.redisClient.getList(key.getKey());
        list.remove(value);
    }
}

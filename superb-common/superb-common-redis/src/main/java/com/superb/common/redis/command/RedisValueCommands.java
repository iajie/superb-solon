package com.superb.common.redis.command;

import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-21 09:01
 */
public interface RedisValueCommands {

    <T> Boolean set(RedisKey key, T value);

    <T> T get(RedisKey key);

    /**
     * 获取缓存信息，没有就存入数据，有则返回，序列化
     * @param key 存入key
     * @param data 存入数据
     * @param get 返回结果
     * @return
     * @param <R>  存入的数据
     * @param <T> 返回的结果
     */
    <R, T> T getNullSet(RedisKey key, Supplier<R> data, Function<RedisUtils, T> get);

}

package com.superb.common.redis.command;

import com.superb.common.redis.key.RedisKey;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-19 10:20
 */
public interface RedisListCommands {

    <T> boolean set(final RedisKey key, final List<T> dataList);


    <T> List<T> get(final RedisKey key);

    <T> List<T> listPage(RedisKey key, int start, int end);

    <T> long listSize(RedisKey key);

    <T> void update(RedisKey key, int index, T value);

    <T> void remove(RedisKey key, T value);
}

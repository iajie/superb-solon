package com.superb.common.redis.command;

import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-20 15:36
 */
public interface RedisHashCommands {

    /**
     * 将哈希表 hash 中域 field 的值设置为 value 已经持有其他值就覆写旧值
     *
     * @param key
     * @param field
     * @param value
     * @param <T>
     * @return
     */
    <T> void set(RedisKey key, String field, T value);

    /**
     * 返回哈希表中给定域的值
     *
     * @param key
     * @param field
     * @return
     */
    <T> T get(RedisKey key, String field);

    /**
     * 返回哈希表中给定域的值
     *
     * @param key
     * @param field
     * @return
     */
    <T> T get(RedisKey key, String field, Class<T> clazz);

    <T> List<T> getArray(RedisKey key, String field, Class<T> clazz);

    <K, V> Map<K, V> getAll(RedisKey key, Set<K> hKeys);

    /**
     * 检查给定域 field 是否存在于哈希表 hash 当中
     *
     * @param key
     * @param field
     * @return
     */
    boolean exists(RedisKey key, String field);


    /**
     * 删除hash表 key 中的一个指定域
     *
     * @param key
     * @param field
     * @return
     */
    void del(RedisKey key, String... field);

    /**
     * 设置hash map
     * @param key
     * @param map
     * @return
     */
    <T> void setAll(RedisKey key, Map<String, T> map);

    /**
     * 将数据存入hash，不存在就存入，存在就取出，可自定义返回结果
     * @param key
     * @param field
     * @param data
     * @param get
     * @return
     * @param <R>
     * @param <T>
     */
    <R, T> T getNullSet(RedisKey key, String field, Supplier<R> data, Function<RedisUtils, T> get);
}

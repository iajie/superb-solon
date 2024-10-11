package com.superb.common.redis.command.impl;

import com.alibaba.fastjson2.JSON;
import com.superb.common.redis.command.RedisHashCommands;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.AbstractSuperbRedis;
import com.superb.common.redis.utils.RedisUtils;
import lombok.SneakyThrows;
import org.redisson.api.RMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-20 15:38
 */
public class RedisHashCommandsImpl extends AbstractSuperbRedis implements RedisHashCommands {
    public RedisHashCommandsImpl(RedisUtils redisUtils) {
        super(redisUtils);
    }

    @Override
    public <T> void set(RedisKey key, String field, T value) {
        RMap<String, T> map = this.redisClient.getMap(key.getKey());
        map.put(field, value);
    }

    @Override
    public <T> T get(RedisKey key, String field) {
        RMap<String, T> map = this.redisClient.getMap(key.getKey());
        return map.get(field);
    }

    @Override
    public <T> T get(RedisKey key, String field, Class<T> clazz) {
        RMap<String, T> map = this.redisClient.getMap(key.getKey());
        return JSON.to(clazz, JSON.toJSONString(map.get(field)));
    }

    @Override
    public <T> List<T> getArray(RedisKey key, String field, Class<T> clazz) {
        RMap<String, T> map = this.redisClient.getMap(key.getKey());
        return JSON.parseArray(JSON.toJSONBytes(map.get(field)), clazz);
    }

    @Override
    public <K, V> Map<K, V> getAll(RedisKey key, Set<K> hKeys) {
        RMap<K, V> map = this.redisClient.getMap(key.getKey());
        return map.getAll(hKeys);
    }

    @Override
    public boolean exists(RedisKey key, String field) {
        RMap<String, Object> map = this.redisClient.getMap(key.getKey());
        return map.containsKey(field);
    }

    @Override
    public void del(RedisKey key, String... fields) {
        RMap<String, Object> map = this.redisClient.getMap(key.getKey());
        for (String field : fields) {
            map.remove(field);
        }
        // hash不存在field项，则删除hash值
        if (map.size() == 0) {
            super.del(key);
        }
    }

    @Override
    public <T> void setAll(RedisKey key, Map<String, T> map) {
        RMap<Object, Object> rMap = this.redisClient.getMap(key.getKey());
        rMap.putAll(map);
    }

    @Override
    @SneakyThrows
    public <R, T> T getNullSet(RedisKey key, String field, Supplier<R> data, Function<RedisUtils, T> get) {
        if (!this.exists(key, field)) {
            R r = data.get();
            if (r != null) {
                this.set(key, field, r);
            }
        }
        return get.apply(this.redisUtils);
    }
}

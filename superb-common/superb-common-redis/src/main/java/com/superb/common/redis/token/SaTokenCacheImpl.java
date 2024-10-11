package com.superb.common.redis.token;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.HeadersUtils;
import org.noear.solon.annotation.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 重写sa-token存储逻辑
 * @Author: ajie
 * @CreateTime: 2024-07-25 17:43
 */
@Component
public class SaTokenCacheImpl implements SaTokenDao {

    @Override
    public String get(String key) {
        return RedisUtils.build().value().get(this.saRedisKey(key));
    }

    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        RedisKey redisKey = this.saRedisKey(key);
        // 判断是否为永不过期
        if (timeout != SaTokenDao.NEVER_EXPIRE) {
            redisKey.setDuration(Duration.ofSeconds(timeout));
        }
        RedisUtils.build().value().set(redisKey, value);
    }

    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    @Override
    public void delete(String key) {
        RedisUtils.build().del(this.saRedisKey(key));
    }

    @Override
    public long getTimeout(String key) {
        long timeout = RedisUtils.build().ttl(this.saRedisKey(key));
        return timeout < 0 ? timeout : timeout / 1000;
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }

        RedisKey redisKey = this.saRedisKey(key, Duration.ofSeconds(timeout));
        // 修改时间--续命机制
        RedisUtils.build().expire(redisKey);
    }

    @Override
    public Object getObject(String key) {
        return RedisUtils.build().value().get(this.saRedisKey(key));
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        RedisKey redisKey = this.saRedisKey(key);
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout != SaTokenDao.NEVER_EXPIRE) {
            redisKey.setDuration(Duration.ofSeconds(timeout));
        }
        RedisUtils.build().value().set(redisKey, object);
    }

    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    @Override
    public void deleteObject(String key) {
        RedisUtils.build().del(this.saRedisKey(key));
    }

    @Override
    public long getObjectTimeout(String key) {
        long timeout = RedisUtils.build().ttl(this.saRedisKey(key));
        return timeout < 0 ? timeout : timeout / 1000;
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        RedisKey redisKey = this.saRedisKey(key);
        redisKey.setDuration(Duration.ofSeconds(timeout));
        RedisUtils.build().expire(redisKey);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Collection<String> keys = RedisUtils.build().keys(HeadersUtils.getTenantId() + "*" + prefix + keyword);
        List<String> list = new ArrayList<>(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }

    private RedisKey saRedisKey(String key, Duration duration) {
        RedisKey redisKey;
        if (key.startsWith(StpUtil.getTokenName())) {
            redisKey = new RedisKey(KeyType.SA_TOKEN, key);
        } else {
            redisKey = new RedisKey();
            redisKey.setKey(key);
            redisKey.setJoin(false);
        }
        if (duration != null) {
            redisKey.setDuration(duration);
        }
        return redisKey;
    }

    private RedisKey saRedisKey(String key) {
        return this.saRedisKey(key, null);
    }
}

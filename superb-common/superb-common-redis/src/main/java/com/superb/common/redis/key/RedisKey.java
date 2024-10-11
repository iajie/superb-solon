package com.superb.common.redis.key;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-20 14:23
 */
@Data
@NoArgsConstructor
public class RedisKey {

    @NonNull
    private KeyType keyType;

    /**
     * 默认缓存时限
     */
    private Duration duration;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * redis实际的Key
     */
    private String key;

    private boolean join = true;

    public RedisKey(@NonNull KeyType keyType, String key) {
        this.keyType = keyType;
        this.key = key;
    }

    public RedisKey(@NonNull KeyType keyType, Duration duration, String key) {
        this.keyType = keyType;
        this.duration = duration;
        this.key = key;
    }

    public RedisKey(String key, Duration duration) {
        this.duration = duration;
        this.keyType = KeyType.NONE;
        this.key = key;
    }

    /**
     * 最终存入redis的key
     * @return
     */
    public String getKey() {
        if (join) {
            if (this.key.contains(KeyType.SA_TOKEN.getName())) {
                return this.key;
            }
            SaRequest request = SaHolder.getRequest();
            this.tenantId = request.getHeader(HeadersUtils.tenantId);
            if (StringUtils.isBlank(this.tenantId)) {
                this.tenantId = HeadersUtils.getTenantId();
            }
            return this.tenantId + ":" + this.keyType + ":" + this.key;
        }
        return this.key;
    }

    public String getRedisKey() {
        return this.key;
    }
}

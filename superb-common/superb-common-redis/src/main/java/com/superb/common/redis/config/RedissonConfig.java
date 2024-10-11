package com.superb.common.redis.config;

import com.superb.common.redis.config.properties.RedisConfigProperties;
import com.superb.common.utils.StringUtils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-25 17:06
 */
@Configuration
public class RedissonConfig {

    /**
     * 根据nacos配置注入redisson连接对象
     * @param redisConfigProperties nacos配置
     * @return
     */
    @Bean
    public RedissonClient redissonClient(@Inject(value = "${superb.redisson}", autoRefreshed = true) RedisConfigProperties redisConfigProperties) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer().setAddress(redisConfigProperties.getAddress());
        if (StringUtils.isNotBlank(redisConfigProperties.getPassword())) {
            config.useSingleServer().setPassword(redisConfigProperties.getPassword());
        }
        return Redisson.create(config);
    }

}

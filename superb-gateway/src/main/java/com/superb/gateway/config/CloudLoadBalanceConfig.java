package com.superb.gateway.config;

import com.superb.common.core.exception.SuperbException;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cloud.impl.CloudLoadStrategy;
import org.noear.solon.cloud.impl.CloudLoadStrategyDefault;
import org.noear.solon.cloud.impl.CloudLoadStrategyIpHash;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

import java.util.List;

/**
 * 注册负载均衡器 https://solon.noear.org/article/478
 * 分布式发现服务 - 负载均衡策略 https://solon.noear.org/article/793
 * @Author: ajie
 * @CreateTime: 2024-07-29 09:21
 */
@Slf4j
@Component
public class CloudLoadBalanceConfig implements CloudLoadStrategy {

    @Inject("${solon.gateway.loadBalance}")
    private String loadBalance;

    /**
     * 返回负载均衡策略 提供：默认为轮询、ip希哈
     * 自定义权重和随机算法
     * @return
     */
    @Bean
    public CloudLoadStrategy loadStrategy() {
        if ("weight".equals(loadBalance)) {
            return new CloudLoadBalanceWeight();
        } else if ("ipHash".equals(loadBalance)) {
            return new CloudLoadStrategyIpHash();
        } else if ("random".equals(loadBalance)) {
            return new CloudLoadBalanceRandom();
        } else {
            return new CloudLoadStrategyDefault();
        }
    }

    /**
     * 灰度指定ip访问
     *
     * @param discovery 节点列表，一个ip多个节点
     * @return
     */
    private String getVersionIp(Discovery discovery, String ip) {
        // 获得指定ip的实例
        List<Instance> cluster = discovery.cluster();
        List<Instance> instances = cluster.stream().filter(i -> i.address().contains(ip)).toList();
        if (instances.isEmpty()) {
            throw new SuperbException(String.format("灰度IP[%s]没有可提供的服务实例!", ip));
        }
        // 构造新的实例
        Discovery ipDiscovery = new Discovery(discovery.group(), discovery.service());
        instances.forEach(ipDiscovery::instanceAdd);
        // 走对应ip负载均衡
        return this.loadStrategy().getServer(ipDiscovery);
    }

    @Override
    public String getServer(Discovery discovery) {
        String versionIp = HeadersUtils.getHeader(HeadersUtils.VERSION_IP, false);
        if (StringUtils.isNotBlank(versionIp)) {
            return this.getVersionIp(discovery, versionIp);
        }
        // 获取请求头信息
        String version = HeadersUtils.getHeader(HeadersUtils.VERSION, false);
        if (StringUtils.isNotBlank(version)) {
            for (Instance instance : discovery.cluster()) {
                if (version.equals(instance.metaGet(HeadersUtils.VERSION))) {
                    return instance.uri();
                }
            }
        }
        return this.loadStrategy().getServer(discovery);
    }
}

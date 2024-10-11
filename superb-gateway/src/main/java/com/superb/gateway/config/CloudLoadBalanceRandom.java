package com.superb.gateway.config;

import cn.hutool.core.util.RandomUtil;
import org.noear.solon.cloud.impl.CloudLoadStrategy;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

import java.util.List;

/**
 * 随机算法
 * 随机负载均衡器
 * @Author: ajie
 * @CreateTime: 2024-08-06 08:39
 */
public class CloudLoadBalanceRandom implements CloudLoadStrategy {

    @Override
    public String getServer(Discovery discovery) {
        // 获取随机最大随机数
        int randomInt = RandomUtil.randomInt(discovery.clusterSize());
        List<Instance> cluster = discovery.cluster();
        return cluster.get(randomInt).uri();
    }
}

package com.superb.gateway.config;

import org.noear.solon.cloud.impl.CloudLoadStrategy;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

import java.util.List;

/**
 * 权重算法
 * 权重负载均衡器
 * @Author: ajie
 * @CreateTime: 2024-08-06 08:39
 */
public class CloudLoadBalanceWeight implements CloudLoadStrategy {

    @Override
    public String getServer(Discovery discovery) {
        // 最大权重
        double maxWeight = 0;
        String server = null;
        List<Instance> cluster = discovery.cluster();
        for (Instance instance : cluster) {
            if (instance.weight() > maxWeight) {
                maxWeight = instance.weight();
                server = instance.uri();
            }
        }
        // 如果所有服务器的权重都为1　
        if (maxWeight == 0) {
            // 重新分配权重
            for (int i = 0; i < cluster.size(); i++) {
                cluster.get(i).weight(i+1);
            }
            for (Instance instance : cluster) {
                if (instance.weight() > maxWeight) {
                    maxWeight = instance.weight();
                    server = instance.uri();
                }
            }
        }
        // 将选中的服务器权重减1
        String finalServer = server;
        cluster.forEach(i -> {
            if (i.uri().equals(finalServer)) {
                i.weight(i.weight()-1);
            }
        });
        return server;
    }
}

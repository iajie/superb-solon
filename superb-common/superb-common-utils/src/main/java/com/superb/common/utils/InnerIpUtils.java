package com.superb.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 内部ip获取和设置
 * @Author: ajie
 * @CreateTime: 2024-05-10 10:43
 */
@Slf4j
public class InnerIpUtils {

    /**
     * 内部服务IP,用于IP鉴权
     */
    private static final CopyOnWriteArraySet<String> serviceIpSet = new CopyOnWriteArraySet<>();
    /**
     * 本地IP
     */
    private static String localIp;

    /**
     * 获取本地IP
     *
     * @return
     */
    public static String getLocalIp() {
        return localIp;
    }

    /**
     * 设置本地IP
     *
     * @param ip
     */
    public static void setLocalIp(String ip) {
        log.info(MessageFormat.format("设置本地IP:{0}", ip));
        localIp = ip;
        add(localIp);
    }

    /**
     * 添加内部ip
     *
     * @param ip
     */
    public static void add(String ip) {
        if (!contains(ip)) {
            serviceIpSet.add(ip);
            log.info(MessageFormat.format("添加内部IP:{0},内部IP列表{1}", ip, serviceIpSet.toString()));
        }
    }

    /**
     * 是否存在ip
     *
     * @param ip
     * @return
     */
    public static Boolean contains(String ip) {
        return serviceIpSet.contains(ip);
    }

    /**
     * 获取内部ip集合
     *
     * @return
     */
    public static Set<String> all() {
        return serviceIpSet;
    }
}

package com.superb.common.utils;

import com.sun.source.tree.IfTree;
import com.superb.common.core.exception.SuperbException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.IPAddress;
import org.noear.solon.core.handle.Context;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-14 09:30
 */
@Slf4j
public class IpUtils {
    /**
     * 逗号_英文
     */
    public static final String COMMA_DBC = ",";
    /**
     * 百分号
     */
    public static final String COMMA_PT = "%";
    /**
     * IP4
     */
    private static final String IP4 = "IP4";
    /**
     * IP6
     */
    private static final String IP6 = "IP6";
    /**
     * IP4本地IP
     */
    public static final String LOCALHOST_IP4 = "127.0.0.1";
    /**
     * IP6本地IP
     */
    private static final String LOCALHOST_IP6 = "0:0:0:0:0:0:0:1";
    /**
     * 框架自定义获取到客户端IP
     */
    public static final String X_SUPERB_IP = "x-superb-ip";
    /**
     * 框架自定义获取到调用方服务IP
     */
    public static final String X_SUPERB_INNER_IP = "x-superb-inner-ip";


    private static String getHeaderIp(Context ctx, String headerName) {
        return ctx.header(headerName);
    }

    private static Boolean checkIp(String ip) {
        return ip == null || !IPAddress.isValid(ip);
    }

    /**
     * 获取客户端IP，代理(ngxin,k8s)
     *
     * @param ctx
     * @return
     */
    public static String getCustomIp(Context ctx) {
        String ip = null;
        // 当内部调用时,获取头部传递的客户端IP
        try {
            String inner = HeadersUtils.getHeader(X_SUPERB_INNER_IP, false);
            // 内网调用时,获取由上一节点传递的外网IP
            if ("true".equals(inner)) {
                ip = getHeaderIp(ctx, X_SUPERB_IP);
            }
        } catch (Exception ex) {
            throw new SuperbException("获取客户端IP异常", ex);
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "X-Original-Forwarded-For");
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "X-Forwarded-For");
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "Proxy-Client-IP");
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "WL-Proxy-Client-IP");
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "HTTP_CLIENT_IP");
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "HTTP_X_FORWARDED_FOR");
        }
        if (checkIp(ip)) {
            ip = getHeaderIp(ctx, "X-Real-IP");
        }
        if (checkIp(ip)) {
            ip = ctx.remoteIp();
            if (LOCALHOST_IP4.equals(ip) || LOCALHOST_IP6.equals(ip)) {
                log.warn("客户端IP获取异常,当前获取到客户端IP为:" + ip);
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    throw new SuperbException("获取客户端IP异常");
                }
                ip = inet.getHostAddress();
            }
        }
        if (checkIp(ip)) {
            ip = ctx.realIp();
        }
//        对于通过多个代理的情况，第一个IP为客户端真实IP, 多个IP按照 ',' 分割
//        "(***.***.***.***" | "0:0:0:0:0:0:0:1").length() = IP4_IP6_Length
//        if (StringUtils.isNotBlank(ip) && ip.length() > IP4_IP6_LENGTH) {
        if (IPAddress.isValid(ip)) {
            if (ip.indexOf(COMMA_DBC) > 0) {
                ip = ip.substring(0, ip.indexOf(COMMA_DBC));
            }
        }
        return ip;
    }


    /**
     * 获取客户端IP，代理(ngxin,k8s)
     *
     * @return
     */
    public static String getCustomIp() {
        Context ctx = Context.current();
        if (ctx != null) {
            return getCustomIp(ctx);
        } else {
            return InnerIpUtils.getLocalIp();
        }
    }

    /**
     * 获取本地IP列表
     *
     * @return
     * @throws SocketException
     */
    public static List<IpEntity> getLocalIps(String... ip46) throws SocketException {
        List<IpEntity> ips = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
//            isLoopback是否为回调接口
//            isUp是否已经开启并运行
//            isVirtual是否虚拟网卡
//            isPointToPoint是否点对点
            if (networkInterface.isLoopback() || networkInterface.isVirtual() || networkInterface.isPointToPoint() || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (StringUtils.isNotEmpty(ip46)) {
                    if (ip46[0].equals(IP4)) {
                        if (inetAddress instanceof Inet4Address) {
                            ips.add(new IpEntity(IP4, inetAddress.getHostName(), inetAddress.getHostAddress()));
                        }
                    }
                    if (ip46[0].equals(IP6)) {
                        if (inetAddress instanceof Inet6Address) {
                            String ip6 = inetAddress.getHostAddress();
                            ips.add(new IpEntity(IP6, inetAddress.getHostName(), ip6.substring(0, ip6.indexOf(COMMA_PT))));
                        }
                    }
                } else {
                    if (inetAddress instanceof Inet4Address) {
                        ips.add(new IpEntity(IP4, inetAddress.getHostName(), inetAddress.getHostAddress()));
                    }
                    if (inetAddress instanceof Inet6Address) {
                        String ip6 = inetAddress.getHostAddress();
                        ips.add(new IpEntity(IP6, inetAddress.getHostName(), ip6.substring(0, ip6.indexOf(COMMA_PT))));
                    }
                }
            }
        }
        return ips;
    }


    @Data
    @AllArgsConstructor
    static
    class IpEntity {
        private String type;
        private String hostName;
        private String hostAddress;
    }
}

package com.superb.common.utils;

import com.superb.common.core.enums.DeviceType;
import com.superb.common.core.exception.SuperbException;
import org.noear.solon.core.handle.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 请求头获取
 * @Author: ajie
 * @CreateTime: 2024-05-10 10:43
 */
public class HeadersUtils {

    public static ThreadLocal<Map<String, String>> localHeader = new ThreadLocal<>();
    public static final String token = "Authorization";
    public static final String tenantId = "tenantId";
    public static final String clientId = "clientId";
    public static final String organId = "organId";
    public static final String feign = "openFeign";
    public static final String gateway = "gateway";
    /**
     * 灰度
     */
    public static final String VERSION = "version";
    /**
     * 指定灰度ip
     */
    public static final String VERSION_IP = "versionIp";

    /**
     * 获取header值
     * @param name header key
     * @param autoException 是否自动处理异常
     * @return
     */
    public static String getHeader(String name, Boolean autoException) {
        String value = null;
        if (localHeader.get() != null && localHeader.get().containsKey(name)) {
            value = localHeader.get().get(name);
        } else {
            Context current = Context.current();
            if (current != null) {
                value = current.header(name);
                if (StringUtils.isBlank(value)) {
                    value = "";
                }
            }
        }
        if (autoException) {
            if (StringUtils.isBlank(value)) {
                throw new SuperbException("headers信息缺失【" + name + "】");
            }
        }
        return value;
    }



    /**
     * 获取header值
     *
     * @param name         键
     * @param defaultValue 默认值
     * @return
     */
    public static String getHeader(String name, Supplier<String> defaultValue) {
        String value = getHeader(name, false);
        if (StringUtils.isBlank(value)) {
            return defaultValue.get();
        }
        return value;
    }


    /**
     * 设置header值
     *
     * @param name  键
     * @param value 值
     * @return
     */
    public static void setHeader(String name, String value) {
        Context current = Context.current();
        if (current != null) {
            current.headerAdd(name, value);
        }
        if (localHeader.get() == null) {
            localHeader.set(new HashMap<>());
        }
        localHeader.get().put(name, value);
    }

    /**
     * 获取token
     * @return
     */
    public static String getToken() {
        String value = getAuthentication();
        if (StringUtils.isBlank(value)) {
            throw new SuperbException("无登录凭证");
        }
        return value;
    }

    /**
     * 获取登录凭证
     * @return 获取请求头携带的登录凭证
     */
    public static String getAuthentication() {
        return getHeader(token, false);
    }

    public static void setAuthentication(String value) {
        setHeader(token, value);
    }

    /**
     * 获取租户id
     * @return 请求头携带的租户id
     */
    public static String getTenantId() {
        return getHeader(tenantId, true);
    }

    /**
     * 获取部门id
     * @return 获取请求头携带的部门id
     */
    public static String getOrganId() {
        return getHeader(organId, false);
    }

    /**
     * 获取部门id
     * @return 获取请求头携带的部门id,如果没有会抛出异常
     */
    public static String getOrganizationId() {
        return getHeader(organId, true);
    }

    public static void setOrganId(String value) {
        setHeader(organId, value);
    }

    public static void setClientId(String value) {
        setHeader(tenantId, value);
    }

    /**
     * 获取客户端id
     * @return 请求头携带的客户端id
     */
    public static String getClientId() {
        return getHeader(clientId, true);
    }

    /**
     * 获取客户端id
     * @return 请求头携带的客户端id
     */
    public static DeviceType getClientType() {
        String header = getHeader(clientId, true);
        return DeviceType.of(header);
    }

    public static void setTenantId(String value) {
        setHeader(clientId, value);
    }

    public static void clear() {
        localHeader.remove();
    }
}

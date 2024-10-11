package com.superb.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.superb.common.core.enums.DataScope;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;

import java.util.List;

/**
 * 数据权限全局线程变量
 * @Author: ajie
 * @CreateTime: 2024-07-12 09:45
 */
public class AuthDataScopeUtils {

    public static ThreadLocal<JSONObject> localHeader = new ThreadLocal<>();

    /**
     * 获取数据权限
     * @return
     */
    public static DataScope dataScope() {
        return getValue("dataScope", DataScope.class, false);
    }

    /**
     * 获取数据权限
     * @return
     */
    public static DataScope dataScope(boolean autoException) {
        return getValue("dataScope", DataScope.class, autoException);
    }

    /**
     * 主动设置数据权限范围
     * @param dataScope
     */
    public static void setDataScopeType(DataScope dataScope) {
        setValue("dataScope", dataScope);
    }

    /**
     * 获取数据权限
     * @return
     */
    public static boolean isTenant() {
        Boolean isTenant = getValue("isTenant", Boolean.class, false);
        return isTenant != null && isTenant;
    }

    /**
     * 是否进行租户查询
     * @param isTenant
     */
    public static void setIsTenant(boolean isTenant) {
        setValue("isTenant", isTenant);
    }

    /**
     * 设置值
     * @param name 名称
     * @param value 设置值
     */
    private static void setValue(String name, Object value) {
        if (localHeader.get() == null) {
            localHeader.set(new JSONObject());
        }
        localHeader.get().put(name, value);
    }

    private static <T> T getValue(String name, Class<T> clazz, boolean autoException) {
        if (localHeader.get() != null && localHeader.get().containsKey(name)) {
            return JSON.to(clazz, localHeader.get().get(name));
        } else {
            if (autoException) {
                throw new SuperbException(SuperbCode.DATA_SCOPE_ERROR);
            }
            return null;
        }
    }

    private static <T> List<T> getArrayValue(String name, Class<T> clazz) {
        if (localHeader.get() != null && localHeader.get().containsKey(name)) {
            return JSONArray.of(localHeader.get().get(name)).toJavaList(clazz);
        } else {
            throw new SuperbException(SuperbCode.DATA_SCOPE_ERROR, "获取数据["+ name +"]缺失");
        }
    }

    /**
     * 清除线程数据
     */
    public static void clear() {
        localHeader.remove();
    }


}

package com.superb.common.core.enums;

/**
 * 指定数据权限范围
 * @Author: ajie
 * @CreateTime: 2024-07-12 09:46
 */
public enum DataScope {

    /**
     * 全部
     */
    ALL,
    /**
     * 无数据权限
     */
    NONE,
    /**
     * 自定义
     */
    CUSTOM,
    /**
     * 本部门
     */
    ORGAN,
    /**
     * 本部门及子部门
     */
    ORGAN_AND_SUB,
    /**
     * 子部门
     */
    ORGAN_SUB,
    /**
     * 本人
     */
    USER;

    public static DataScope ofValue(Integer type) {
        return switch (type) {
            case 1 -> ALL;
            case 2 -> ORGAN;
            case 3 -> ORGAN_AND_SUB;
            case 4 -> CUSTOM;
            case 5 -> ORGAN_SUB;
            default -> USER;
        };
    }
}

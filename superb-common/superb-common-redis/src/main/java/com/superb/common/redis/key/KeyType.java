package com.superb.common.redis.key;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-20 14:29
 */
public enum KeyType {

    /**
     * 未设置缓存类型
     */
    NONE("none"),
    /**
     * 有时限的缓存
     */
    TIME("time"),
    /**
     * 有时限的缓存
     */
    SA_TOKEN("satoken"),
    /**
     * 永久保存的缓存
     */
    PER("per"),
    /**
     * 异步写入的缓存
     */
    PUSH("push"),
    /**
     * 启动初始化的缓存
     */
    INIT("init"),
    /**
     * 方法注解的缓存
     */
    MEHTOD("method"),
    /**
     * 分布式锁
     */
    LOCK("lock");

    private final String name;

    KeyType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

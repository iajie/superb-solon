package com.superb.common.database.annotation;

import com.superb.common.database.annotation.handler.IgnoreTenantHandler;
import org.noear.solon.annotation.Before;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不查询租户-sql排除租户id
 * @Author: ajie
 * @CreateTime: 2024-07-18 17:55
 */
@Before(IgnoreTenantHandler.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SuperbIgnoreTenant {

    /**
     * 是否注入tenant_id
     * @return
     */
    boolean value() default true;

}

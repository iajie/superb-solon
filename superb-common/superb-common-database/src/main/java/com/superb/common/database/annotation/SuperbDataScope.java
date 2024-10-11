package com.superb.common.database.annotation;

import com.superb.common.core.enums.DataScope;

import java.lang.annotation.*;

/**
 * 自定义数据权限控制，不受登录用户限制，强制覆盖
 * @Author: ajie
 * @CreateTime: 2024-07-12 11:09
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SuperbDataScope {

    /**
     * 数据权限范围,默认无数据权限
     * @return
     */
    DataScope scope() default DataScope.NONE;

}

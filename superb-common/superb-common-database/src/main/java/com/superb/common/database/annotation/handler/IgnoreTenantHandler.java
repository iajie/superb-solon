package com.superb.common.database.annotation.handler;

import com.superb.common.database.annotation.SuperbIgnoreTenant;
import com.superb.common.utils.AuthDataScopeUtils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-01 15:34
 */
public class IgnoreTenantHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        Action action = ctx.action();
        BeanWrap controller = action.controller();
        // 获取数据权限注解
        SuperbIgnoreTenant superbIgnoreTenant = controller.annotationGet(SuperbIgnoreTenant.class);
        if (superbIgnoreTenant != null) {
            AuthDataScopeUtils.setIsTenant(superbIgnoreTenant.value());
        }
        // 4.当前动作的执行方法包装
        MethodWrap method = action.method();
        SuperbIgnoreTenant annotation = method.getAnnotation(SuperbIgnoreTenant.class);
        if (annotation != null) {
            AuthDataScopeUtils.setIsTenant(annotation.value());
        }
    }

}

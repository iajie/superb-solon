package com.superb.common.database.annotation.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.superb.common.core.enums.DataScope;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.security.configure.TokenConfigure;
import com.superb.common.security.entity.SuperbUserDataScope;
import com.superb.common.security.utils.SuperbUtils;
import com.superb.common.utils.AuthDataScopeUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.PatternParserUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptorChain;

import java.lang.reflect.Method;

/**
 * 处理数据权限和租户查询
 * https://solon.noear.org/article/216
 * https://solon.noear.org/article/502 案例1
 * @Author: ajie
 * @CreateTime: 2024-07-31 15:00
 */
@Component
@Condition(onClass = TokenConfigure.class)
public class DataScopeRouteInterceptor extends TokenConfigure {

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (!PatternParserUtils.isMatch(this.excludeList, ctx.path()) && StpUtil.isLogin()) {
            // 1.获取当前动作对象
            Action action = ctx.action();
            if (action == null) {
                throw new SuperbException(SuperbCode.REQUEST_HEADERS_ERROR, "请检查请求地址或方法是否正确");
            }
            // 2.获取当前控制器实例
            BeanWrap controller = action.controller();
            // 获取数据权限注解
            SuperbDataScope superbDataScope = controller.annotationGet(SuperbDataScope.class);
            if (superbDataScope!= null) {
                // 将类上注解赋值
                AuthDataScopeUtils.setDataScopeType(superbDataScope.scope());
            }
            // 4.当前动作的执行方法包装
            Method method = action.method().getMethod();
            boolean methodSuperbDataScope = method.isAnnotationPresent(SuperbDataScope.class);
            if (methodSuperbDataScope) {
                SuperbDataScope annotation = method.getAnnotation(SuperbDataScope.class);
                // 将类上注解赋值
                AuthDataScopeUtils.setDataScopeType(annotation.scope());
            }
            // 5.未设置权限注解，才进入用户权限
            if (AuthDataScopeUtils.dataScope() == null) {
                HeadersUtils.getAuthentication();
                // 获取用户当前数据权限范围
                SuperbUserDataScope userDataScope = SuperbUtils.getUserDataScope();
                // 如果没有数据权限，设置为本人
                if (userDataScope.getDataScopeType() == null) {
                    userDataScope.setDataScopeType(0);
                }
                // 设置数据权限
                DataScope dataScope = DataScope.ofValue(userDataScope.getDataScopeType());
                AuthDataScopeUtils.setDataScopeType(dataScope);
            }
        }
        // 让链路进行下去
        super.doIntercept(ctx, mainHandler, chain);
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        if (result instanceof Result<?> res) {
            if (res.getResult() instanceof Page<?> pageRes) {
                com.superb.common.database.entity.Page<?> data = new com.superb.common.database.entity.Page<>();
                BeanUtil.copyProperties(pageRes, data);
                data.setTotal(pageRes.getTotalRow());
                return Result.success(data);
            }
        }
        return result;
    }
}

package com.superb.common.security.filter;

import cn.dev33.satoken.exception.*;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.core.model.Result;
import com.superb.common.utils.AuthDataScopeUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.auth.AuthException;
import org.noear.solon.auth.AuthStatus;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.validation.ValidatorException;
import org.noear.solon.validation.annotation.NoRepeatSubmit;

import java.lang.annotation.Annotation;
import java.sql.SQLException;

/**
 * 全局过滤器，包含限流和异常
 * @Author: ajie
 * @CreateTime: 2024-07-25 17:51
 */
@Slf4j
@Component(index = 1)
public class GlobalFilter implements Filter {

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            // 判断是否为gateway网关转发过来
            String gateway = ctx.header(HeadersUtils.gateway);
            if ("superb-gateway".equals(gateway)) {
                // TODO 非网关转发...
            }
            chain.doFilter(ctx);
        } catch (BreakerException ex) {
            log.error("限流异常", ex);
            // 限流
            ctx.render(Result.error(SuperbCode.SENTINEL_LIMIT, ex.getMessage()));
        } catch (ValidatorException e) {
            ctx.setHandled(false);
            log.error("参数校验异常-->{}", e.getResult().getDescription());
            // 重复提交提示
            NoRepeatSubmit annotation = (NoRepeatSubmit) e.getAnnotation();
            if (annotation != null) {
                ctx.render(Result.error("操作频繁，请稍后再试!"));
            } else {
                ctx.render(Result.error(e.getResult().getDescription()));
            }
        } catch (SaTokenException e) {
            log.error("sa-token异常", e);
            // 鉴权异常
            if (e instanceof NotLoginException ee) {
                if (StringUtils.isBlank(HeadersUtils.getAuthentication())) {
                    ctx.render(Result.error(SuperbCode.TOEKN_NOT_LOGIN, "无登录凭证"));
                } else {
                    String message = ee.getMessage();
                    if (message.contains("被顶下线")) {
                        ctx.render(Result.error(SuperbCode.AUTH_OFFLINE_ERROR, "当前账号已在其他地方登录！"));
                    } else if (message.contains("被踢下线")) {
                        ctx.render(Result.error(SuperbCode.AUTH_OFFLINE_ERROR, "当前账号已下线，请重新登录！"));
                    } else if (message.contains("token 无效")) {
                        ctx.render(Result.error(SuperbCode.AUTH_ERROR, "登录凭证无效！"));
                    } else {
                        ctx.render(Result.error(SuperbCode.TOEKN_NOT_LOGIN, ee.getMessage()));
                    }
                }
            } else if (e instanceof NotRoleException ee) {
                ctx.render(Result.error(SuperbCode.TOEKN_ROLE, ee.getMessage()));
            } else if (e instanceof ApiDisabledException ee) {
                ctx.render(Result.error(SuperbCode.API_DISABLED, ee.getMessage()));
            } else if (e instanceof NotPermissionException ee) {
                ctx.render(Result.error(SuperbCode.TOKEN_PERMISSION, ee.getMessage()));
            } else if (e instanceof DisableServiceException ee) {
                ctx.render(Result.error(SuperbCode.TOKEN_DISABLED, ee.getMessage()));
            } else if (e instanceof NotSafeException ee) {
                ctx.render(Result.error(SuperbCode.TOKEN_SAFE, ee.getMessage()));
            } else {
                ctx.render(Result.error("鉴权异常", e.getMessage()));
            }
        } catch (AuthException e) {
            log.error("Solon鉴权异常", e);
            AuthStatus status = e.getStatus();
            if (status == AuthStatus.OF_IP) {
                ctx.render(Result.error("IP黑名单", e.getMessage()));
            } else {
                ctx.render(Result.error("鉴权异常", e.getMessage()));
            }
        } catch (SuperbException e) {
            log.error("自定义异常！", e);
            if (e.getCode() == null) {
                ctx.render(Result.error(e.getMessage()));
            } else {
                ctx.render(Result.error(e.getCode(), e.getMessage()));
            }
        } catch (SQLException e) {
            log.error("SQL异常！", e);
            ctx.render(Result.error("SQL异常！", e.getMessage()));
        } catch (Exception ex) {
            log.error("程序错误！", ex);
            ctx.render(Result.error("服务端异常", ex.getMessage()));
        }
        // 清除线程数据，避免内存泄漏
        AuthDataScopeUtils.clear();
        HeadersUtils.clear();
        long times = System.currentTimeMillis() - start;
        log.info("请求【{}】【{}】完成，耗时:【{}ms】", ctx.path(), ctx.method(), times);
    }

}

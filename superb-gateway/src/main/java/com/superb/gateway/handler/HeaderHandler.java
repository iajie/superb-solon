package com.superb.gateway.handler;

import com.superb.common.core.enums.DeviceType;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.PatternParserUtils;
import com.superb.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-29 09:10
 */
@Slf4j
@Component
public class HeaderHandler implements Handler {

    @Inject("${superb.custom.ignoreServiceUrl}")
    private Set<String> ignoreServiceUrl = new HashSet<>();
    @Inject("${superb.custom.ignoreUrl}")
    private Set<String> ignoreUrl = new HashSet<>();

    @Override
    public void handle(Context ctx) throws Throwable {
        String clientId = HeadersUtils.getHeader(HeadersUtils.clientId, false);
        String tenantId = HeadersUtils.getHeader(HeadersUtils.tenantId, false);
        String token = HeadersUtils.getHeader(HeadersUtils.token, false);
        if (PatternParserUtils.isMatch(ignoreUrl, ctx.path())) {
            if (StringUtils.isBlank(tenantId)) {
                throw new SuperbException(SuperbCode.REQUEST_ERROR, "无租户凭证");
            } else if (StringUtils.isBlank(clientId)) {
                throw new SuperbException(SuperbCode.REQUEST_ERROR, "无客户端凭证");
            } else if (StringUtils.isNotBlank(clientId)) {
                DeviceType of = DeviceType.of(clientId);
            } else if (StringUtils.isBlank(token)) {
                // 校验地址是否白名单 nacos
                if (PatternParserUtils.isMatch(ignoreServiceUrl, ctx.path())) {
                    throw new SuperbException(SuperbCode.AUTH_ERROR, "无登录凭证");
                }
            }
        }

    }
}

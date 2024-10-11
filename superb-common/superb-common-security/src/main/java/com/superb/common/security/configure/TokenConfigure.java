package com.superb.common.security.configure;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.superb.common.security.properties.SecurityProperties;
import com.superb.common.security.service.AuthProcessorImpl;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import org.noear.snack.core.utils.DateUtil;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.auth.AuthAdapter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;

import java.util.Date;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-25 17:49
 */
@Configuration
public class TokenConfigure extends SaTokenInterceptor {

    @Bean
    public SecurityProperties securityProperties(@Inject(value = "${superb.custom}", autoRefreshed = true) SecurityProperties securityProperties) {
        return securityProperties;
    }

    @Bean
    public SaTokenInterceptor saTokenInterceptor(SecurityProperties properties) {
        return new SaTokenInterceptor()
            .addInclude("/**")
            .addExclude(properties.getIgnoreUrl().toArray(String[]::new))
            .addExclude(properties.getIgnoreServiceUrl().stream().map(i -> i.substring(StringUtils.getIndex("/", i, 2))).toArray(String[]::new))
            .setAuth(auth -> SaRouter.match("/**").check(this::setLoginUser));
    }

    /**
     * 登录用户信息赋值
     */
    private void setLoginUser() {
        StpUtil.checkLogin();
        SaSession session = StpUtil.getSession();
        // 1. 获取登录客户端列表
        List<TokenSign> signs = session.getTokenSignList();
        // 2. 获取当前操作客户端信息
        signs.forEach(item -> {
            if (item.getDevice().equals(HeadersUtils.getClientId())) {
                // 3. 设置客户端信息
                JSONObject tag = (JSONObject)item.getTag();
                // 最后活跃时间
                tag.put("lastActiveTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                // 活跃数据权限部门
                tag.put("organId", HeadersUtils.getOrganId());
                item.setTag(tag);
            }
        });
        session.setTokenSignList(signs);
        session.update();
    }

    /**
     * 鉴权处理（可以在每个模块定制合适的鉴权处理）
     * @return
     */
    @Bean
    @Condition(onMissingBean = AuthAdapter.class)
    public AuthAdapter authAdapter(SecurityProperties securityProperties) {
        AuthProcessorImpl authProcessor = new AuthProcessorImpl(securityProperties.getIps());
        return new AuthAdapter()
                // 设定鉴权处理器
                .processor(authProcessor)
                // 设定默认的验证失败处理
                .failure(Context::render);
    }
}

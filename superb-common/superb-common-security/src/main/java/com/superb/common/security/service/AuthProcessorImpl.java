package com.superb.common.security.service;

import cn.dev33.satoken.stp.StpUtil;
import lombok.AllArgsConstructor;
import org.noear.solon.auth.AuthProcessor;
import org.noear.solon.auth.annotation.Logical;

import java.util.Set;

/**
 * solon鉴权实现
 * 只做ip和登录校验，其他交给sa-token完成
 * @Author: ajie
 * @CreateTime: 2024-07-26 08:52
 */
@AllArgsConstructor
public class AuthProcessorImpl implements AuthProcessor {

    private final Set<String> ips;

    @Override
    public boolean verifyIp(String ip) {
        return !ips.contains(ip);
    }

    @Override
    public boolean verifyLogined() {
        return StpUtil.isLogin();
    }

    @Override
    public boolean verifyPath(String path, String method) {
        return true;
    }

    @Override
    public boolean verifyPermissions(String[] permissions, Logical logical) {
        return true;
    }

    @Override
    public boolean verifyRoles(String[] roles, Logical logical) {
        return true;
    }
}

package com.superb.common.security.utils;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.IpUtils;
import org.noear.snack.core.utils.DateUtil;

import java.util.Date;

/**
 * 登录工具类
 * @Author: ajie
 * @CreateTime: 2024-07-26 09:12
 */
public class LoginUtils {

    /**
     * sa-token登录
     * @param loginId 登录id
     * @param loginType 登录方式
     */
    public static void login(Object loginId, Integer loginType) {
        SaLoginModel loginModel = new SaLoginModel();
        // 登录客户端、允许多个
        loginModel.setDevice(HeadersUtils.getClientType().name());
        // 登录端信息
        JSONObject signTag = new JSONObject();
        // 登录类型
        signTag.put("loginType", loginType);
        // 登录时间
        signTag.put("loginTime", DateUtil.format(new Date(), DateUtil.FORMAT_19_a));
        // 登录ip
        signTag.put("loginIp", IpUtils.getCustomIp());
        // 最后活跃时间
        signTag.put("lastActiveTime", DateUtil.format(new Date(), DateUtil.FORMAT_19_a));
        // 设置登录端数据
        loginModel.setTokenSignTag(signTag);
        StpUtil.login(loginId, loginModel);
    }

}

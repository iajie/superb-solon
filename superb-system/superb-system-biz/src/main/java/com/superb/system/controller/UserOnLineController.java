package com.superb.system.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.superb.common.core.enums.DeviceType;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.system.api.dto.UserOnLine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Path;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-23 11:17
 */
@Valid
@Controller
@SuperbDataScope
@Api("用户在线情况")
@Mapping("userOnline")
public class UserOnLineController {

    @Mapping(value = "list/{userId}", method = MethodType.GET)
    @ApiOperation(value = "获取用户登录列表")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true)
    public Result<List<UserOnLine>> onlineList(@Path String userId) {
        List<UserOnLine> list = new ArrayList<>();
        // 1. 根据用户id获取会话
        SaSession session = StpUtil.getSessionByLoginId(userId);
        // 2. 根据会话列表获取登录信息
        List<TokenSign> signs = session.getTokenSignList();
        for (TokenSign sign : signs) {
            JSONObject tag = (JSONObject)sign.getTag();
            UserOnLine userOnLine = tag.to(UserOnLine.class);
            userOnLine.setDeviceType(sign.getDevice());
            userOnLine.setToken(sign.getValue());
            list.add(userOnLine);
        }
        return Result.success(list);
    }

    @Mapping(value = "logout/{token}", method = MethodType.GET)
    @ApiOperation(value = "注销登录用户")
    @ApiImplicitParam(name = "token", value = "用户登录凭证", required = true)
    public Result<List<UserOnLine>> logout(@Path String token) {
        StpUtil.logoutByTokenValue(token);
        return Result.success();
    }

    @Mapping(value = "kickout/{token}", method = MethodType.GET)
    @ApiOperation(value = "注销登录用户")
    @ApiImplicitParam(name = "token", value = "用户登录凭证", required = true)
    public Result<List<UserOnLine>> kickout(@Path String token) {
        StpUtil.kickoutByTokenValue(token);
        return Result.success();
    }

}

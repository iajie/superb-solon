package com.superb.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.system.api.dto.Token;
import com.superb.system.api.dto.ValidateImage;
import com.superb.system.api.entity.SystemTenant;
import com.superb.system.api.vo.PhoneCodeLogin;
import com.superb.system.api.vo.PwdLogin;
import com.superb.system.service.SystemTenantService;
import com.superb.system.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.time.Duration;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-14 09:24
 */
@Valid
@Controller
@SuperbDataScope
@Api("登录管理")
@Mapping("admin/login")
public class LoginController {

    @Inject
    private SystemUserService userService;
    @Inject
    private SystemTenantService tenantService;

    @ApiOperation(value = "获取验证码")
    @Mapping(value = "validateImage/{type}", method = MethodType.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "验证码类型：random随机字符串、calc计算")
    })
    public Result<ValidateImage> validateImage(@Path String type) {
        String code;
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 45, 4, 2);
        if (type.equals("calc")) {
            // 四则运算
            captcha.setGenerator(new MathGenerator(1));
            captcha.createCode();
            code = String.valueOf(((int) Calculator.conversion(captcha.getCode())));
        } else {
            code = captcha.getCode().toLowerCase();
        }
        String key = RandomUtil.randomString(16);
        // 验证码默认60s有效期
        RedisKey redisKey = new RedisKey(KeyType.TIME, Duration.ofMinutes(1), key);
        // 存入验证码
        RedisUtils.build().value().set(redisKey, code);
        ValidateImage validateImage = new ValidateImage();
        validateImage.setKey(key);
        validateImage.setData(captcha.getImageBase64Data());

        return Result.success(validateImage);
    }

    @Mapping(value = "validateCode/{phone}", method = MethodType.GET)
    @ApiImplicitParam(name = "phone", required = true, value = "获取手机号")
    public Result<ValidateImage> validateCode(@Path String phone) {
        int code = RandomUtil.randomInt(1000, 9999);
        // 验证码默认60s有效期
        RedisKey redisKey = new RedisKey(KeyType.TIME, Duration.ofMinutes(5), "validate::" + phone);
        // 存入验证码
        RedisUtils.build().value().set(redisKey, code);
        return Result.success();
    }

    @Mapping(value = "pwdLogin", method = MethodType.POST)
    @ApiOperation(value = "账号密码登录", notes = "系统用户账号密码登录")
    public Result<Token> pwdLogin(@Body @Validated PwdLogin login) {
        return Result.success(userService.pwdLogin(login));
    }

    @Mapping(value = "phoneCodeLogin", method = MethodType.POST)
    @ApiOperation(value = "手机号验证码登录")
    public Result<Token> phoneCodeLogin(@Body @Validated PhoneCodeLogin login) {
        Token token = userService.phoneCodeLogin(login);
        return Result.success(token);
    }

    @Mapping(value = "loginOut", method = MethodType.GET)
    @ApiOperation(value = "退出登录")
    public Result<Boolean> loginOut() {
        StpUtil.logout(StpUtil.getLoginId(), HeadersUtils.getClientId());
        return Result.success("退出登录成功！");
    }

    @Mapping(value = "tenantInfo", method = MethodType.GET)
    @ApiOperation(value = "获取租户信息")
    public Result<SystemTenant> info() {
        RedisKey key = new RedisKey(KeyType.PER, "tenantInfo");
        String tenantId = HeadersUtils.getTenantId();
        SystemTenant tenant = RedisUtils.build().hash().getNullSet(key, tenantId, () -> {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(SystemTenant::getTenantKey, tenantId);
            return tenantService.getOne(queryWrapper);
        }, utils -> utils.hash().get(key, tenantId));
        return Result.success(tenant);
    }

    @Mapping(value = "clearCache", method = MethodType.GET)
    @ApiOperation(value = "清除当前操作人的缓存")
    public Result<Boolean> clearCache() {
        String userId = StpUtil.getLoginId().toString();
        RedisKey key = new RedisKey(KeyType.TIME,"userCache:" + userId);
        RedisKey key1 = new RedisKey(KeyType.TIME,"user:" + userId);
        RedisKey key3 = new RedisKey(KeyType.PER,"menuCache:" + userId);
        // 用户部门列表和数据权限列表
        RedisKey key4 = new RedisKey(KeyType.TIME,"organizationIds:" + HeadersUtils.getOrganId());
        RedisKey key5 = new RedisKey(KeyType.TIME,"organization:" + HeadersUtils.getOrganId());
        RedisKey key2 = new RedisKey(KeyType.PER,"userInfo:" + userId + "*");
        RedisUtils.build().del(key, key1, key2, key3, key4, key5);
        RedisUtils.build().dels(key2);
        return Result.success();
    }

}

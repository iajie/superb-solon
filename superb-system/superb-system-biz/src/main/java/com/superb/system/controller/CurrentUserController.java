package com.superb.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.redis.annotation.SuperbLock;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.PasswordUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.dto.UserInfo;
import com.superb.system.api.entity.SystemUser;
import com.superb.system.api.vo.UpdateAccount;
import com.superb.system.api.vo.UpdateCurrent;
import com.superb.system.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-03 10:47
 */
@Valid
@Controller
@Api("当前用户")
@Mapping("current/user")
public class CurrentUserController {

    @Inject
    private SystemUserService userService;

    @SuperbDataScope
    @Mapping(method = MethodType.GET)
    @ApiOperation(value = "当前登录用户信息", notes = "根据当前登录人获取缓存信息")
    public Result<UserInfo> userInfo(@Param(defaultValue = "") String scopeId) {
        return Result.success(userService.getCurrentUser(scopeId));
    }

    @SuperbLock("updateUserBase")
    @Mapping(value = "updateBase", method = MethodType.POST)
    @ApiOperation(value = "修改本人基础信息")
    public Result<Boolean> updateAvatar(@Body @Validated UpdateCurrent current) {
        String userId = StpUtil.getLoginId().toString();
        SystemUser user = BeanUtil.copyProperties(current, SystemUser.class);
        user.setId(userId);
        if (StringUtils.isNotBlank(current.getPhoneNumber())) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(SystemUser::getPhoneNumber, current.getPhoneNumber())
                    .ne(SystemUser::getId, userId)
                    .eq(SystemUser::getTenantId, HeadersUtils.getTenantId());
            if (userService.count(queryWrapper) > 0) {
                return Result.error("当前手机号已注册！");
            }
        }
        if (StringUtils.isNotBlank(current.getIdcard())) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(SystemUser::getIdcard, current.getIdcard())
                    .ne(SystemUser::getId, userId)
                    .eq(SystemUser::getTenantId, HeadersUtils.getTenantId());
            if (userService.count(queryWrapper) > 0) {
                return Result.error("当前身份证号已被使用！");
            }
        }
        if (StringUtils.isNotBlank(current.getEmail())) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(SystemUser::getEmail, current.getEmail())
                    .ne(SystemUser::getId, userId)
                    .eq(SystemUser::getTenantId, HeadersUtils.getTenantId());
            if (userService.count(queryWrapper) > 0) {
                return Result.error("当前邮箱地址已被使用！");
            }
        }
        if (userService.updateById(user)) {
            RedisKey key = new RedisKey(KeyType.TIME, "userCache:" + userId);
            RedisUtils.build().del(key);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "updateAccount", method = MethodType.POST)
    @ApiOperation(value = "修改本人登录账号")
    public Result<Boolean> updateAccount(@Body @Validated(value = UpdateAccount.Username.class) UpdateAccount account) {
        String userId = StpUtil.getLoginId().toString();
        SystemUser user = userService.getInfoById(userId);
        String decrypt = PasswordUtils.decrypt(user.getSalt(), user.getPassword());
        if (!decrypt.equals(account.getPassword())) {
            throw new SuperbException(SuperbCode.LOGIN_USER_PASSWORD);
        }
        SystemUser systemUser = new SystemUser();
        systemUser.setId(userId);
        systemUser.setUsername(account.getUsername());
        if (userService.updateById(systemUser)) {
            RedisKey key = new RedisKey(KeyType.TIME, "userCache:" + userId);
            RedisUtils.build().del(key);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "updatePassword", method = MethodType.POST)
    @ApiOperation(value = "修改本人登录密码")
    public Result<Boolean> updatePassword(@Body @Validated(value = UpdateAccount.Password.class) UpdateAccount account) {
        String userId = StpUtil.getLoginId().toString();
        SystemUser user = userService.getInfoById(userId);
        String salt = user.getSalt();
        String password = salt + account.getPassword();
        String decrypt = PasswordUtils.decrypt(user.getSalt(), user.getPassword());
        if (!decrypt.equals(account.getPassword())) {
            return Result.error("旧密码错误，请检查后输入！");
        }
        SystemUser systemUser = new SystemUser();
        systemUser.setId(userId);
        // 10位随机数，作为登录密码的秘钥
        String newSalt = RandomUtil.randomString(PasswordUtils.BASE_SALT, 16);
        systemUser.setPassword(PasswordUtils.encrypt(newSalt, account.getNewPassword()));
        systemUser.setSalt(newSalt);
        if (userService.updateById(systemUser)) {
            return Result.success();
        }
        return Result.error();
    }

}

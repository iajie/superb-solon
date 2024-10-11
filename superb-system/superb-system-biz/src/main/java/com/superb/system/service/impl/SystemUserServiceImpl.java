package com.superb.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.security.utils.LoginUtils;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.PasswordUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.dto.DataScope;
import com.superb.system.api.dto.Token;
import com.superb.system.api.dto.User;
import com.superb.system.api.dto.UserInfo;
import com.superb.system.api.entity.SystemUser;
import com.superb.system.api.vo.PhoneCodeLogin;
import com.superb.system.api.vo.PwdLogin;
import com.superb.system.mapper.SystemUserMapper;
import com.superb.system.service.SystemPermissionService;
import com.superb.system.service.SystemRoleService;
import com.superb.system.service.SystemUserService;
import com.superb.system.service.UserDataScopeService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.time.Duration;
import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-29 14:49
 */
@Component
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    @Inject
    private SystemRoleService roleService;
    @Inject
    private SystemPermissionService permissionService;
    @Inject
    private UserDataScopeService dataScopeService;

    @Override
    public boolean checkUsername(String username) {
        if (StringUtils.isNotEmpty(username)) {
            return super.count(this.queryChain().eq(SystemUser::getUsername, username)) > 0;
        }
        return false;
    }

    @Override
    public boolean checkPhoneNumber(String phone) {
        if (StringUtils.isNotEmpty(phone)) {
            return super.count(this.queryChain().eq(SystemUser::getPhoneNumber, phone)) > 0;
        }
        return false;
    }

    @Override
    public boolean checkEmail(String email) {
        if (StringUtils.isNotEmpty(email)) {
            return super.count(this.queryChain().eq(SystemUser::getEmail, email)) > 0;
        }
        return false;
    }

    @Override
    public SystemUser getInfoById(String userId) {
        RedisKey key = new RedisKey(KeyType.TIME, Duration.ofHours(5),"user:" + userId);
        return RedisUtils.build().value().getNullSet(key, () -> super.getById(userId), redis -> redis.value().get(key));
    }

    @Override
    public Token pwdLogin(PwdLogin login) {
        RedisKey redisKey = new RedisKey(KeyType.TIME, login.getKey());
        if (!RedisUtils.build().exists(redisKey)) {
            throw new SuperbException(SuperbCode.CAPTCHA_OVERDUE);
        }
        // 校验验证码
        String code = RedisUtils.build().value().get(redisKey);
        if (!login.getCode().equals(code.toLowerCase())) {
            throw new SuperbException(SuperbCode.CAPTCHA_ERROR);
        }
        RedisUtils.build().del(redisKey);
        QueryWrapper query = new QueryWrapper();
        if (login.getType() == 0) {
            query.eq(SystemUser::getUsername, login.getUsername());
        } else {
            query.eq(SystemUser::getPhoneNumber, login.getUsername());
        }
        SystemUser user = super.getOne(query);
        if (user == null) {
            throw new SuperbException(SuperbCode.LOGIN_USER_NULL);
        }
        if (user.getStatus() != 0) {
            throw new SuperbException(SuperbCode.LOGIN_USER_ENABLED);
        }
        // 校验账号密码是否正确
        String salt = user.getSalt();
        if (!login.getPassword().equals(PasswordUtils.decrypt(salt, user.getPassword()))) {
            throw new SuperbException(SuperbCode.LOGIN_USER_PASSWORD);
        }
        LoginUtils.login(user.getId(), login.getType());
        return new Token(StpUtil.getTokenName(), StpUtil.getTokenValue());
    }

    @Override
    public Token phoneCodeLogin(PhoneCodeLogin login) {
        RedisKey redisKey = new RedisKey(KeyType.TIME, "validate:" + login.getPhoneNumber());
        if (!RedisUtils.build().exists(redisKey)) {
            throw new SuperbException(SuperbCode.CAPTCHA_OVERDUE);
        }
        // 校验验证码
        String code = RedisUtils.build().value().get(redisKey);
        if (!login.getCode().equals(code)) {
            throw new SuperbException(SuperbCode.CAPTCHA_ERROR);
        }
        QueryChain<SystemUser> queryChain = super.queryChain().eq(SystemUser::getPhoneNumber, login.getPhoneNumber());
        SystemUser user = super.getOne(queryChain);
        if (user == null) {
            throw new SuperbException(SuperbCode.LOGIN_NOT_PHONE);
        }
        if (user.getStatus() != 0) {
            throw new SuperbException(SuperbCode.LOGIN_USER_ENABLED);
        }
        LoginUtils.login(user.getId(), 2);
        return new Token(StpUtil.getTokenName(), StpUtil.getTokenValue());
    }

    @Override
    public UserInfo getCurrentUser(String scopeId) {
        String userId = StpUtil.getLoginId().toString();
        RedisKey key = new RedisKey(KeyType.TIME, Duration.ofMinutes(30), "userCache:" + userId);
        return RedisUtils.build().value().getNullSet(key, () -> {
            UserInfo info = new UserInfo();
            User user = super.getOneAs(super.query().eq("id", userId), User.class);
            info.setUser(user);
            // 数据权限列表
            List<DataScope> options = dataScopeService.listByUserId(userId);
            // 设置默认部门
            if (!options.isEmpty()) {
                // 设置请求头，后续节点用到
                if (StringUtils.isBlank(HeadersUtils.getOrganId())) {
                    HeadersUtils.setHeader(HeadersUtils.organId, options.get(0).getOrganId());
                }
                info.setDataScopes(options);
            } else {
                throw new SuperbException("当前用户不存在默认部门，请联系管理员");
            }
            info.setRoles(roleService.listByUserId(userId));
            info.setPermissions(permissionService.listByUserId(userId));
            // 获取菜单
            info.setTopMenu(permissionService.topMenuList(userId));
            info.setLeftMenu(permissionService.sideMenuList(userId));
            return info;
        }, utils -> utils.value().get(key));
    }
}

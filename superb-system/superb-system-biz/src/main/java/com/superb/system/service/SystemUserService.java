package com.superb.system.service;

import com.mybatisflex.core.service.IService;
import com.superb.system.api.dto.Token;
import com.superb.system.api.dto.UserInfo;
import com.superb.system.api.entity.SystemUser;
import com.superb.system.api.vo.PhoneCodeLogin;
import com.superb.system.api.vo.PwdLogin;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-29 14:49
 */
public interface SystemUserService extends IService<SystemUser> {

    boolean checkUsername(String username);

    boolean checkPhoneNumber(String phone);

    boolean checkEmail(String email);

    SystemUser getInfoById(String userId);

    Token pwdLogin(PwdLogin login);

    Token phoneCodeLogin(PhoneCodeLogin login);

    UserInfo getCurrentUser(String scopeId);
}

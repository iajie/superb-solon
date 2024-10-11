package com.superb.common.core.enums;

import lombok.Getter;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-13 10:31
 */
@Getter
public enum SuperbCode {

    REQUEST_ERROR(500, "请求异常"),
    TENANT_ENABLED(522, "租户已禁用"),
    AUTH_ERROR(406, "网关鉴权异常"),
    AUTH_404(404, "接口不存在"),
    AUTH_OFFLINE_ERROR(407, "账号状态异常"),
    REQUEST_HEADERS_ERROR(500, "请求异常"),
    LOGIN_USER_NULL(521, "当前登录账户不存在！"),
    USER_NULL(521, "用户不存在！"),
    TENANT_NULL(521, "租户不存在！"),
    ORGANIZ_NULL(521, "部门不存在！"),
    LOGIN_USER_PASSWORD(522, "用户密码错误！"),
    LOGIN_USER_ENABLED(523, "当前用户已禁用！"),
    LOGIN_NOT_PHONE(524, "当前手机号未注册！"),
    CAPTCHA_ERROR(525, "验证码错误"),
    CAPTCHA_OVERDUE(526, "验证码已过期"),
    DATA_SCOPE_ERROR(450, "数据权限异常"),
    DATA_SCOPE_ORGAN(451, "数据权限部门获取异常"),
    /**
     * sa-token异常
     */
    TOEKN_ROLE(401, "权限不足！"),
    TOEKN_NOT_LOGIN(400, "账号未登录！"),
    TOEKN_ERROR(411, "登录凭证已失效"),
    TOKEN_PERMISSION(403, "权限不足！"),
    TOKEN_SAFE(403, "权限不足！"),
    TOKEN_DISABLED(402, "账号封禁！"),
    API_DISABLED(402, "接口封禁！"),
    AES_ENCRYPT(511, "数据库加密异常！"),
    AES_DECRYPT(512, "数据库解密异常！"),
    REDIS_DATABASE(500, "redis索引越界异常"),
    ALIYUN_OSS_UPLOAD(305, "阿里云 OSS 上传异常"),
    ALIYUN_OSS_REPEAT(306, "文件已存在，请勿重复上传"),
    ALIYUN_OSS_CLIENT(304, "阿里云 OSS 连接异常"),
    SENTINEL_LIMIT(400, "访问频繁"),
    BEAN_CREATE(500, "Solon Bean创建异常"),
    /*** flowable code ***/
    FLOWABLE_ERROR(601, "流程实例异常");
    private final int code;
    private String message;

    SuperbCode(int code) {
        this.code = code;
    }

    SuperbCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

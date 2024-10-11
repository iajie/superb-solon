package com.superb.common.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-29 15:03
 */
public class PasswordUtils {

    public static final String BASE_SALT = "SuperbAjiez01654+/=*";

    /**
     * 加密
     * @param slat 盐
     * @param password 明文密码
     * @return
     */
    public static String encrypt(String slat, String password) {
        // 生成秘钥
        byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), slat.getBytes()).getEncoded();
        // 返回加密
        AES aes = SecureUtil.aes(encoded);
        return aes.encryptBase64(password);
    }

    /**
     * 加密
     * @param slat 盐
     * @param password 明文密码
     * @return
     */
    public static String decrypt(String slat, String password) {
        // 生成秘钥
        byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), slat.getBytes()).getEncoded();
        // 返回解密
        AES aes = SecureUtil.aes(encoded);
        return aes.decryptStr(password);
    }
}

package com.bridge.lifecycle.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
public class PasswordUtils {

    /**
     * 密码加密（MD5）
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        return DigestUtil.md5Hex(password);
    }

    /**
     * 密码验证
     *
     * @param rawPassword     原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String encryptedPassword) {
        // 简化版：直接比较（与原版IndexedDB一致）
        return rawPassword.equals(encryptedPassword);
    }

    /**
     * 生成默认密码
     *
     * @param username 用户名
     * @return 默认密码
     */
    public static String generateDefaultPassword(String username) {
        return username + "123";
    }
}
package com.sscl.baselibrary.utils;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 *
 * @author jackie
 */
@SuppressWarnings("unused")
public class Md5Util {
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };

    @SuppressWarnings("unused")
    public static String encrypt(@NonNull String sourceText) {

        byte[] btInput = sourceText.getBytes();
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst = null;
        try {
            mdInst = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (mdInst == null){
            return null;
        }
        // 使用指定的字节更新摘要
        mdInst.update(btInput);
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            str[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        return new String(str);
    }
}

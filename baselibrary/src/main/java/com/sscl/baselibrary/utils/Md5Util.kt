package com.sscl.baselibrary.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * MD5加密工具类
 *
 * @author jackie
 */
object Md5Util {
    private val HEX_DIGITS = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    )

    fun encrypt(sourceText: String): String? {
        val btInput = sourceText.toByteArray()
        // 获得MD5摘要算法的 MessageDigest 对象
        var mdInst: MessageDigest? = null
        try {
            mdInst = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        if (mdInst == null) {
            return null
        }
        // 使用指定的字节更新摘要
        mdInst.update(btInput)
        // 获得密文
        val md = mdInst.digest()
        // 把密文转换成十六进制的字符串形式
        val j = md.size
        val str = CharArray(j * 2)
        var k = 0
        for (byte0 in md) {
            str[k++] = HEX_DIGITS[byte0.toInt() ushr 4 and 0xf]
            str[k++] = HEX_DIGITS[byte0.toInt() and 0xf]
        }
        return String(str)
    }
}
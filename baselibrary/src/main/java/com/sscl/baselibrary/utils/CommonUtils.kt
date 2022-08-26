package com.sscl.baselibrary.utils

import androidx.annotation.IntRange
import androidx.annotation.Size
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * 属性声明
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

private const val IP_V4_REGEX =
    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"

/**
 * MAC地址的长度
 */
private const val ADDRESS_BYTE_LENGTH = 6

/**
 * Long类型字节数组最大值
 */
private const val MAX_LONG_BYTE_ARRAY_LENGTH = 8

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * 方法声明
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/**
 * 将字符串转为ASCII码十六进制字符串
 */
fun String?.toAsciiHexString(): String? {
    this ?: return null
    val byteArray = toByteArray(StandardCharsets.US_ASCII)
    return byteArray.toHexStringWithSpace()
}

/**
 * 十六进制字符串转换为标准字符串
 * 十六进制字符串之间没有任何分割符，如:[616C6B]
 */
fun String?.toStandardString(): String? {
    this ?: return null
    val str = "0123456789ABCDEF"
    val cache: CharArray = toCharArray()
    val byteArray = ByteArray(length / 2)
    var n: Int
    for (i in byteArray.indices) {
        n = str.indexOf(cache[2 * i]) * 16
        n += str.indexOf(cache[2 * i + 1])
        byteArray[i] = (n and 0xff).toByte()
    }
    return String(byteArray)
}

/**
 * 十六进制字符串转换为标准字符串
 * 十六进制字符串之间没有任何分割符，如:[616C6B]
 */
fun String?.fromHexStringWithoutDelimiterToByteArray(): ByteArray? {
    this ?: return null
    var m: Int
    var n: Int
    val l: Int = length / 2
    val ret = ByteArray(l)
    for (i in 0 until l) {
        m = i * 2 + 1
        n = m + 1
        val integer: Int = Integer.decode("0x" + substring(i * 2, m) + substring(m, n))
        ret[i] = integer.toByte()
    }
    return ret
}

/**
 * 将IPV4字符串转为byte数组
 */
fun String?.toIpV4ByteArray(): ByteArray? {
    this ?: return null
    if (!isIpv4String()) {
        return null
    }
    val split: Array<String> = split("\\.").toTypedArray()
    val result = ByteArray(4)
    for (i in result.indices) {
        result[i] = split[i].toInt().toByte()
    }
    return result
}

/**
 * 字节数组转十六进制字符串，每个字节间用空格分割
 */
fun ByteArray?.toHexStringWithSpace(): String? {
    return toHexStringWithDelimiter(" ")
}

/**
 * 字节数组转十六进制字符串，每个字节间用指定分隔符分割
 */
fun ByteArray?.toHexStringWithDelimiter(delimiter: String): String? {
    this ?: return null
    var stmp: String
    val sb = StringBuilder()
    for (aByte in this) {
        stmp = Integer.toHexString(aByte.toInt() and 0xFF)
        sb.append(if (stmp.length == 1) "0$stmp" else stmp)
        sb.append(delimiter)
    }
    return sb.toString().uppercase(Locale.getDefault()).trim()
}

/**
 * 字节数组转MAC地址字符串
 */
fun ByteArray?.toMacAddressString(): String? {
    if (this?.size != ADDRESS_BYTE_LENGTH) {
        return null
    }
    val toHexStringWithSpace = toHexStringWithSpace() ?: return null
    val addressCache = toHexStringWithSpace.replace(" ", ":")
    return addressCache.uppercase(Locale.getDefault())
}

/**
 * 将带分隔符的十六进制字符串转为byte数组
 *
 * @return byte数组
 */
fun String?.fromHexStringWithSpaceToByteArray(): ByteArray? {
    return fromHexStringWithDelimiterToByteArray(" ")
}

/**
 * 将带分隔符的十六进制字符串转为byte数组
 *
 * @return byte数组
 */
fun String?.fromHexStringWithDelimiterToByteArray(delimiter: String): ByteArray? {
    this ?: return null
    //将获取到的数据以空格为间隔截取成数组
    val split: Array<String> = split(delimiter).toTypedArray()
    val length: Int = split.size

    //新建一个byte数组
    val result = ByteArray(length)
    var cache: Int
    //用循环使截取出来的16进制字符串数组中的字符串转为byte,并固定for循环次数为定义的长度(这样的话，即使String数组长度大于定义的长度，也不会发生数组越界异常)
    for (i in result.indices) {
        try {
            cache = split[i].toInt(16)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        }
        result[i] = cache.toByte()
    }
    return result
}

/**
 * byte数组转为long
 *
 * @return long
 */
fun ByteArray?.toLong(): Long? {
    this ?: return null
    val length = size
    val byteBits = 8
    if (isEmpty() || size > MAX_LONG_BYTE_ARRAY_LENGTH) {
        throw RuntimeException("byteArray length must be in range 1 ~ 8")
    }
    var cache: Long = 0
    for (i in 0 until length) {
        val aByte: Byte = get(i)
        val unsignedByte: Int = ConversionUtil.getUnsignedByte(aByte)
        cache = cache or unsignedByte.toLong() shl (byteBits * (length - i - 1))
    }
    return cache
}

/**
 * 检查是否符合IPv4格式文本
 *
 * @return true表示符合
 */
fun String?.isIpv4String(): Boolean {
    this ?: return false
    val pattern = Pattern.compile(IP_V4_REGEX)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

/**
 * 字符串转换成byte数组（数组长度最长为byteArrayLength）
 *
 * @param charset         编码方式
 * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
 * @return 转换后获得的byte[]
 */
fun String?.getByteArray(
    charset: Charset = StandardCharsets.UTF_8,
    @Size(min = 0) byteArrayLength: Int = this?.length ?: 0
): ByteArray? {
    this ?: return null
    val data: ByteArray
    if (byteArrayLength > 0) {
        if (length > byteArrayLength) {
            data = ByteArray(byteArrayLength)
            System.arraycopy(toByteArray(charset), 0, data, 0, byteArrayLength)
        } else {
            data = toByteArray(charset)
        }
    } else {
        data = toByteArray(charset)
    }
    return data
}

/**
 * 字符串转换成byte数组（数组长度最长为byteArrayLength）
 *
 * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
 * @return 转换后获得的byte[]
 */
fun String?.getByteArray(
    @Size(min = 0) byteArrayLength: Int = this?.length ?: 0
): ByteArray? {
    return getByteArray(StandardCharsets.UTF_8, byteArrayLength)
}

/**
 * 字符串转换成byte数组，自动判断中文简体语言环境，在中文简体下，自动以GBK方式转换（数组长度最长为byteArrayLength）
 *
 * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
 * @return 转换后获得的byte[]
 */
fun String?.getByteArrayAutoGbk(byteArrayLength: Int): ByteArray? {
    return if (isZhCn()) {
        getByteArray(Charset.forName("GBK"), byteArrayLength)
    } else {
        getByteArray(byteArrayLength)
    }
}

/**
 * 判断当前语言环境是否为中文环境
 */
fun isZhCn(): Boolean {
    val aDefault = Locale.getDefault()
    val aDefaultStr = aDefault.toString()
    val zhCn = "zh_CN"
    return zhCn.lowercase() == aDefaultStr.lowercase()
}

/**
 * 将Long类型转为字节数组
 */
fun Long?.toByteArray(): ByteArray? {
    this ?: return null
    val byteLength = 2
    var hexString: String = java.lang.Long.toHexString(this)
    val length: Int = hexString.length
    if (length % byteLength == 0) {
        val byteArray = ByteArray(length / byteLength)
        for (i in byteArray.indices) {
            val cacheString: String = hexString.substring(i * byteLength, i * byteLength + 2)
            val cache: Short = cacheString.toShort(16)
            byteArray[i] = cache.toByte()
        }
        return byteArray
    } else {
        val byteArray = ByteArray(length / byteLength + 1)
        val substring: String = hexString.substring(0, 1)
        byteArray[0] = substring.toShort(16).toByte()
        hexString = hexString.substring(1)
        for (i in 0 until byteArray.size - 1) {
            val cacheString: String = hexString.substring(i * byteLength, i * byteLength + 2)
            val cache: Short = cacheString.toShort(16)
            byteArray[i + 1] = cache.toByte()
        }
        return byteArray
    }
}

/**
 * 将 long 类型数转为指定长度的 byte 数组
 *
 * @param byteArrayLength 指定的数组长度
 * @return byte 数组
 */
fun Long?.toByteArray(@IntRange(from = 1, to = 8) byteArrayLength: Int): ByteArray? {
    var bytes: ByteArray = toByteArray() ?: return null
    val length: Int = bytes.size
    if (length < byteArrayLength) {
        val index: Int = byteArrayLength - length
        val cache = ByteArray(byteArrayLength)
        System.arraycopy(bytes, 0, cache, index, length)
        bytes = ByteArray(byteArrayLength)
        System.arraycopy(cache, 0, bytes, 0, byteArrayLength)
    } else if (length > byteArrayLength) {
        val index: Int = length - byteArrayLength
        val cache = ByteArray(byteArrayLength)
        System.arraycopy(bytes, index, cache, 0, byteArrayLength)
        bytes = ByteArray(byteArrayLength)
        System.arraycopy(cache, 0, bytes, 0, cache.size)
    }
    return bytes
}


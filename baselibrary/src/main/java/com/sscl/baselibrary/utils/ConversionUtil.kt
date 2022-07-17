package com.sscl.baselibrary.utils

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import androidx.annotation.Size
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.text.DecimalFormat
import java.util.*

/**
 * 数据转换工具类
 *
 * @author pengh
 */
object ConversionUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 静态常量 * * * * * * * * * * * * * * * * * * */

    /**
     * 设备地址的长度
     */
    private const val ADDRESS_BYTE_LENGTH: Int = 6

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    fun strToHexStr(str: String): String {
        val chars: CharArray = "0123456789ABCDEF".toCharArray()
        val sb: StringBuilder = StringBuilder()
        val bs: ByteArray = str.toByteArray()
        var bit: Int
        for (b: Byte in bs) {
            bit = (b.toInt() and 0x0f0) ushr 4
            sb.append(chars[bit])
            bit = b.toInt() and 0x0f
            sb.append(chars[bit])
            sb.append(' ')
        }
        return sb.toString().trim { it <= ' ' }
    }

    /**
     * 将IP v4的字符串转为byte数组
     *
     * @param ipv4 IP v4的字符串
     * @return byte数组
     */
    @Size(4)
    fun ipv4StringToByteArray(ipv4: String): ByteArray? {
        if (!Tool.checkIpv4String(ipv4)) {
            return null
        }
        val split: Array<String> = ipv4.split("\\.".toRegex()).toTypedArray()
        val result = ByteArray(4)
        for (i in result.indices) {
            result[i] = split[i].toInt().toByte()
        }
        return result
    }

    /**
     * 字符串转换成byte数组（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    fun getByteArray(s: String, @Size(min = 0) byteArrayLength: Int): ByteArray {
        return getByteArray(s, Charset.defaultCharset(), byteArrayLength)
    }

    /**
     * 字符串转换成byte数组（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param charsetName     编码方式的名字
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     * @throws UnsupportedCharsetException 不支持的编码类型
     */
    @Throws(UnsupportedCharsetException::class)
    fun getByteArray(
        s: String,
        charsetName: String,
        @Size(min = 0) byteArrayLength: Int
    ): ByteArray {
        val charset: Charset = Charset.forName(charsetName)
        return getByteArray(s, charset, byteArrayLength)
    }

    /**
     * 字符串转换成byte数组（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param charset         编码方式
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    fun getByteArray(s: String, charset: Charset, @Size(min = 0) byteArrayLength: Int): ByteArray {
        val data: ByteArray
        if (byteArrayLength > 0) {
            if (s.length > byteArrayLength) {
                data = ByteArray(byteArrayLength)
                System.arraycopy(s.toByteArray(charset), 0, data, 0, byteArrayLength)
            } else {
                data = s.toByteArray(charset)
            }
        } else {
            data = s.toByteArray(charset)
        }
        return data
    }

    /**
     * 字符串转换成byte数组，自动判断中文简体语言环境，在中文简体下，自动以GBK方式转换（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    fun getByteArrayAutoGbk(s: String, byteArrayLength: Int): ByteArray {
        return if (Tool.isZhCn) {
            getByteArray(s, "GBK", byteArrayLength)
        } else {
            getByteArray(s, byteArrayLength)
        }
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    fun hexStrToStr(hexStr: String): String {
        val str = "0123456789ABCDEF"
        val cache: CharArray = hexStr.toCharArray()
        val byteArray = ByteArray(hexStr.length / 2)
        var n: Int
        for (i in byteArray.indices) {
            n = str.indexOf(cache[2 * i]) * 16
            n += str.indexOf(cache[2 * i + 1])
            byteArray[i] = (n and 0xff).toByte()
        }
        return String(byteArray)
    }

    /**
     * byteArray转换成十六进制字符串
     *
     * @param byteArray byte数组
     * @return String 每个Byte值之间空格分隔
     */
    fun byteArrayToHexStr(byteArray: ByteArray): String {
        var stmp: String
        val sb: StringBuilder = StringBuilder()
        for (aByte: Byte in byteArray) {
            stmp = Integer.toHexString(aByte.toInt() and 0xFF)
            sb.append(if ((stmp.length == 1)) "0$stmp" else stmp)
            sb.append(" ")
        }
        return sb.toString().uppercase(Locale.getDefault()).trim { it <= ' ' }
    }

    /**
     * 将长整形转为byte数组
     *
     * @param value 长整形
     * @return byte数组
     */
    fun longToByteArray(value: Long): ByteArray {
        val byteLength = 2
        var hexString: String = java.lang.Long.toHexString(value)
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
     * 将long类型数（0~0x0000FFFFFFFFFFFF之间）转为6字节byte数组
     *
     * @param value 0~0x0000FFFFFFFFFFFF之间的long类型数
     * @return 6字节byte数组
     */
    @Size(6)
    fun longToByteArrayLength6(@Size(min = 0, max = 0x0000FFFFFFFFFFFFL) value: Long): ByteArray {
        val byteArrayLength = 6
        val byteArray: ByteArray = longToByteArray(value)
        if (byteArray.size == byteArrayLength) {
            return byteArray
        }
        val result = ByteArray(byteArrayLength)
        System.arraycopy(byteArray, 0, result, 6 - byteArray.size, byteArray.size)
        return result
    }

    /**
     * 将 long 类型数转为指定长度的 byte 数组
     *
     * @param value           long 类型的数据
     * @param byteArrayLength 指定的数组长度
     * @return byte 数组
     */
    fun longToByteArray(value: Long, @IntRange(from = 1, to = 8) byteArrayLength: Int): ByteArray {
        var bytes: ByteArray = longToByteArray(value)
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

    /**
     * byteArray字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    @Throws(NumberFormatException::class)
    fun hexStrToByteArray(src: String): ByteArray {
        var m: Int
        var n: Int
        val l: Int = src.length / 2
        println(l)
        val ret = ByteArray(l)
        for (i in 0 until l) {
            m = i * 2 + 1
            n = m + 1
            val integer: Int = Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n))
            ret[i] = integer.toByte()
        }
        return ret
    }

    /**
     * byte数组转为long
     *
     * @param byteArray byte数组
     * @return long
     */
    fun byteArrayToLong(@Size(min = 1, max = 8) byteArray: ByteArray): Long {
        val length: Int = byteArray.size
        val maxLongByteArrayLength = 8
        val byteBits = 8
        if (byteArray.isEmpty() || byteArray.size > maxLongByteArrayLength) {
            throw RuntimeException("byteArray length must be in range 1 ~ 8")
        }
        var cache: Long = 0
        for (i in 0 until length) {
            val aByte: Byte = byteArray.get(i)
            val unsignedByte: Int = getUnsignedByte(aByte)
            cache = cache or unsignedByte.toLong() shl (byteBits * (length - i - 1))
        }
        return cache
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @param strText 全角字符串
     * @return String 每个unicode之间无分隔符
     */
    fun strToUnicode(strText: String): String {
        var c: Char
        val str: StringBuilder = StringBuilder()
        var intAsc: Int
        var strHex: String?
        for (i in strText.indices) {
            c = strText[i]
            intAsc = c.code
            strHex = Integer.toHexString(intAsc)
            if (intAsc > 128) {
                str.append("\\u").append(strHex)
            } else {
                str.append("\\u00").append(strHex)
            }
        }
        return str.toString()
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    fun unicodeToString(hex: String): String {
        val t: Int = hex.length / 6
        val str: StringBuilder = StringBuilder()
        for (i in 0 until t) {
            val s: String = hex.substring(i * 6, (i + 1) * 6)
            // 高位需要补上00再转
            val s1: String = s.substring(2, 4) + "00"
            // 低位直接转
            val s2: String = s.substring(4)
            // 将16进制的string转为int
            val n: Int = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16)
            // 将int转换为字符
            val chars: CharArray = Character.toChars(n)
            str.append(String(chars))
        }
        return str.toString()
    }

    /**
     * 将一个byte数组拼接为一个int型数
     *
     * @param byteArray byte数组长度不超过4
     * @return int型数
     */
    fun byteArrayToInt(@Size(min = 1, max = 4) byteArray: ByteArray): Int {
        val length: Int = byteArray.size
        val maxLongByteArrayLength = 4
        val byteBits = 8
        if (byteArray.isEmpty() || byteArray.size > maxLongByteArrayLength) {
            throw RuntimeException("byteArray length must be in range 1 ~ 4")
        }
        var cache = 0
        for (i in 0 until length) {
            val aByte: Byte = byteArray.get(i)
            val unsignedByte: Int = getUnsignedByte(aByte)
            cache = cache or unsignedByte shl (byteBits * (length - i - 1))
        }
        return cache
    }

    /**
     * 格式化数字为千分位显示；
     *
     * @param number 要格式化的数字
     * @return
     */
    fun formatMicrometer(number: String): String {
        val df: DecimalFormat
        val point = "."
        df = if (number.indexOf(point) > 0) {
            when (number.length - number.indexOf(point) - 1) {
                0 -> {
                    DecimalFormat("###,##0.")
                }
                1 -> {
                    DecimalFormat("###,##0.0")
                }
                else -> {
                    DecimalFormat("###,##0.00")
                }
            }
        } else {
            DecimalFormat("###,##0")
        }
        val result: Double = try {
            number.toDouble()
        } catch (e: Exception) {
            0.0
        }
        return df.format(result)
    }

    /**
     * 将一个整数转换成2个字节的byte数组
     *
     * @param i 整数
     * @return 2个字节的byte数组
     */
    @Size(2)
    fun intToByteArrayLength2(i: Int): ByteArray {
        val hexString: String = intToHexStr(i)
        val highByte: Byte
        val lowByte: Byte
        val hexStringMinLength = 2
        if (hexString.length > hexStringMinLength) {
            var substring: String = hexString.substring(0, hexString.length - 2)
            highByte = substring.toInt(16).toByte()
            substring = hexString.substring(hexString.length - 2)
            lowByte = substring.toInt(16).toByte()
        } else {
            highByte = 0
            lowByte = hexString.toInt(16).toByte()
        }
        return byteArrayOf(highByte, lowByte)
    }

    /**
     * 将一个整数转换成4个字节的byte数组
     *
     * @param n 整数
     * @return 4个字节的byte数组
     */
    @Size(4)
    fun intToByteArrayLength4(n: Int): ByteArray {
        val b = ByteArray(4)
        for (i in b.indices) {
            b[i] = (n ushr 24 - i * 8).toByte()
        }
        return b
    }

    /**
     * 将整数转换成16进制字符串
     *
     * @param i 整数
     * @return 16进制字符串
     */
    fun intToHexStr(i: Int): String {
        return Integer.toHexString(i)
    }

    /**
     * 将字节型数据转换为0~255 (0xFF 即BYTE)
     *
     * @param data data字节型数据
     * @return 无符号的整型
     */
    fun getUnsignedByte(data: Byte): Int {
        return data.toInt() and 0x0FF
    }

    /**
     * 将字节型数据转换为0~65535 (0xFFFF 即 WORD)
     *
     * @param data 字节型数据
     * @return 无符号的整型
     */
    fun getUnsignedShort(data: Short): Int {
        return data.toInt() and 0x0FFFF
    }

    /**
     * 将带空格的十六进制字符串转为byte数组
     *
     * @param hexStr 带空格的十六进制字符串
     * @return byte数组
     */
    fun hexStringToByteArray(hexStr: String): ByteArray? {
        //将获取到的数据以空格为间隔截取成数组
        val split: Array<String> = hexStr.split(" ".toRegex()).toTypedArray()
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
     * 将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
     *
     * @param data int数据
     * @return 无符号的长整型
     */
    fun getUnsignedInt(data: Int): Long {
        //获取最低位
        val lowBit: Int = (1 and data).toByte().toInt()
        //无符号右移一位（无符号数）
        val i: Int = data ushr 1
        //将右移之后的数强转为long之后重新左移回去
        val l: Long = i.toLong() shl 1
        //重新加上低位的值
        return l + lowBit
    }

    /**
     * 将int转为boolean(0 = false ,1 = true)
     *
     * @param value int值
     * *
     * @return 对应的结果
     */
    fun intToBoolean(value: Int): Boolean {
        return when (value) {
            0 -> false
            1 -> true
            else -> throw RuntimeException("The error value $value")
        }
    }

    /**
     * 将boolean转为int(true = 1,false = 0)
     *
     * @param b boolean值
     * *
     * @return 对应的int值
     */
    fun booleanToInt(b: Boolean): Int {
        return if (b) {
            1
        } else {
            0
        }
    }

    /**
     * 使用代码触发home键的效果
     *
     * @param context 上下文
     */
    fun pressHomeButton(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        // 注意:必须加上这句代码，否则就不是单例了
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(intent)
    }

    /**
     * 检测byte数组中的内容有效性（全0为无效）
     *
     * @param byteArray byte数组
     * @return true表示有效
     */
    fun checkByteValid(byteArray: ByteArray): Boolean {
        for (aByte: Byte in byteArray) {
            if (aByte.toInt() != 0) {
                return true
            }
        }
        return false
    }

    /**
     * 将任意对象转为byte数组
     *
     * @param o 任意对象
     * @return byte数组
     */
    fun objectToByteArray(o: Any): ByteArray? {
        var byteArray: ByteArray? = null
        val byteArrayOutputStream = ByteArrayOutputStream()
        var objectOutputStream: ObjectOutputStream? = null
        try {
            objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(o)
            byteArray = byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        try {
            byteArrayOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return byteArray
    }

    /**
     * 将数组类型转为指定的对象
     *
     * @param byteArray 数组类
     * @return T 指定对象
     */
    fun <T : Serializable?> byteArrayToObject(byteArray: ByteArray): T? {
        var o: T? = null
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        var objectInputStream: ObjectInputStream? = null
        try {
            objectInputStream = ObjectInputStream(byteArrayInputStream)
            val readObject = objectInputStream.readObject()
            o = readObject as T?
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            try {
                byteArrayInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return o
    }

    /**
     * 将蓝牙设备地址转为byte数组
     *
     * @param address 设备地址
     * @return byte数组
     */
    fun bluetoothAddressStringToByteArray(address: String): ByteArray? {
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return null
        }
        val cacheArray: Array<String> = address.split(":".toRegex(), 6).toTypedArray()
        val bluetoothByteArray = ByteArray(6)
        for (i in cacheArray.indices) {
            val cache: String = cacheArray[i]
            var integer: Int
            try {
                integer = Integer.valueOf(cache, 16)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                return null
            }
            bluetoothByteArray[i] = integer.toByte()
        }
        return bluetoothByteArray
    }

    /**
     * 将设备地址数组转为设备地址字符串
     *
     * @param addressByteArray 设备地址数组
     * @return 设备地址字符串（AA:AA:AA:AA:AA:AA）
     */
    fun bluetoothAddressByteArrayToString(addressByteArray: ByteArray): String? {
        if (addressByteArray.size != ADDRESS_BYTE_LENGTH) {
            return null
        }
        val addressCacheString: String = byteArrayToHexStr(addressByteArray)
        val addressCache: String = addressCacheString.replace(" ", ":")
        return addressCache.uppercase(Locale.getDefault())
    }

    /**
     * 将一个int型的Ip地址转为点分式地址字符串
     *
     * @param ip int型的Ip地址
     * @return 点分式字符串
     */
    fun intIp4ToStringIp4(ip: Int): String {
        return (ip and 0xFF).toString() + "." + ((ip shr 8) and 0xFF) + "." + ((ip shr 16) and 0xFF) + "." + ((ip shr 24) and 0xFF)
    }

    /**
     * 将一个int型的Ip地址转为点分式地址字符串
     *
     * @param ip int型的Ip地址
     * @return 点分式字符串
     */
    fun intIp4ToReverseStringIp4(ip: Int): String {
        return (((ip shr 24) and 0xFF).toString() + "."
                + ((ip shr 16) and 0xFF) + "."
                + ((ip shr 8) and 0xFF) + "."
                + (ip and 0xFF))
    }
}
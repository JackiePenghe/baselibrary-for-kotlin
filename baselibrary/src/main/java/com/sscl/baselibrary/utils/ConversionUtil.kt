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


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
        @Suppress("UNCHECKED_CAST")
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
    fun macAddressStringToByteArray(address: String): ByteArray? {
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
package com.sscl.baselibrary.utils

import java.math.BigDecimal

/**
 * Android 精确算法
 * 适用于金额、利率换算
 */
object BigDecimalUtils {
    /**
     * 提供精确的加法运算
     *
     * @param addend1 被加数
     * @param addend2 加数
     * @param scale   保留scale 位小数
     * @return 两个参数的和
     */
    fun add(addend1: String?, addend2: String?, scale: Int): String {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b1 = BigDecimal(addend1)
        val b2 = BigDecimal(addend2)
        return b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 提供精确的减法运算
     *
     * @param subtracted 被减数
     * @param minus      减数
     * @param scale      保留scale 位小数
     * @return 两个参数的差
     */
    fun sub(subtracted: String?, minus: String?, scale: Int): String {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b1: BigDecimal = BigDecimal(subtracted)
        val b2: BigDecimal = BigDecimal(minus)
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 提供精确的乘法运算
     *
     * @param multiplier1 被乘数
     * @param multiplier2 乘数
     * @param scale       保留scale 位小数
     * @return 两个参数的积
     */
    fun mul(multiplier1: String?, multiplier2: String?, scale: Int): String {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b1: BigDecimal = BigDecimal(multiplier1)
        val b2: BigDecimal = BigDecimal(multiplier2)
        return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 提供精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    表示需要精确到小数点以后几位
     * @return 两个参数的商
     */
    fun div(dividend: String?, divisor: String?, scale: Int): String {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b1: BigDecimal = BigDecimal(dividend)
        val b2: BigDecimal = BigDecimal(divisor)
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param value 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    fun round(value: Double, scale: Int): Double {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b: BigDecimal = BigDecimal(value)
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param value 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    fun round(value: String?, scale: Int): String {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b: BigDecimal = BigDecimal(value)
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 取余数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    小数点后保留几位
     * @return 余数
     */
    fun remainder(dividend: String?, divisor: String?, scale: Int): String {
        if (scale < 0) {
            throw RuntimeException("scale value must be greater or equal to 0")
        }
        val b1: BigDecimal = BigDecimal(dividend)
        val b2: BigDecimal = BigDecimal(divisor)
        return b1.remainder(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
    }

    /**
     * 比较大小
     *
     * @param value1 被比较数
     * @param value2 比较数
     * @return 如果v1 大于v2 则 返回true 否则false
     */
    fun compare(value1: String?, value2: String?): Boolean {
        val b1: BigDecimal = BigDecimal(value1)
        val b2: BigDecimal = BigDecimal(value2)
        val bj: Int = b1.compareTo(b2)
        return bj > 0
    }

    /**
     * 比较大小
     *
     * @param value1 被比较数
     * @param value2 比较数
     * @return 如果v1 大于或等于 v2 则 返回true 否则false
     */
    fun compareWithEqual(value1: String?, value2: String?): Boolean {
        val b1: BigDecimal = BigDecimal(value1)
        val b2: BigDecimal = BigDecimal(value2)
        val bj: Int = b1.compareTo(b2)
        return bj >= 0
    }
}
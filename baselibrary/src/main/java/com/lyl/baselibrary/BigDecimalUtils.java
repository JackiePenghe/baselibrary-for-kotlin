package com.lyl.baselibrary;

import java.math.BigDecimal;

/**
 * Android 精确算法
 * 适用于金额、利率换算
 */
public class BigDecimalUtils {
    /**
     * 提供精确的加法运算
     *
     * @param addend1 被加数
     * @param addend2 加数
     * @param scale   保留scale 位小数
     * @return 两个参数的和
     * @throws Exception scale的值必须大于等于0
     */
    public static String add(String addend1, String addend2, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b1 = new BigDecimal(addend1);
        BigDecimal b2 = new BigDecimal(addend2);
        return b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 提供精确的减法运算
     *
     * @param subtracted 被减数
     * @param minus      减数
     * @param scale      保留scale 位小数
     * @return 两个参数的差
     * @throws Exception scale的值必须大于等于0
     */
    public static String sub(String subtracted, String minus, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b1 = new BigDecimal(subtracted);
        BigDecimal b2 = new BigDecimal(minus);
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param multiplier1 被乘数
     * @param multiplier2 乘数
     * @param scale       保留scale 位小数
     * @return 两个参数的积
     * @throws Exception scale的值必须大于等于0
     */
    public static String mul(String multiplier1, String multiplier2, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b1 = new BigDecimal(multiplier1);
        BigDecimal b2 = new BigDecimal(multiplier2);
        return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 提供精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    表示需要精确到小数点以后几位
     * @return 两个参数的商
     * @throws Exception scale的值必须大于等于0
     */
    public static String div(String dividend, String divisor, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b1 = new BigDecimal(dividend);
        BigDecimal b2 = new BigDecimal(divisor);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param value 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     * @throws Exception scale的值必须大于等于0
     */
    public static double round(double value, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b = new BigDecimal(value);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param value     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     * @throws Exception scale的值必须大于等于0
     */
    public static String round(String value, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b = new BigDecimal(value);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 取余数
     *
     * @param dividend    被除数
     * @param divisor    除数
     * @param scale 小数点后保留几位
     * @return 余数
     * @throws Exception scale的值必须大于等于0
     */
    public static String remainder(String dividend, String divisor, int scale) throws Exception {
        if (scale < 0) {
            throw new Exception("scale value must be greater or equal to 0");
        }
        BigDecimal b1 = new BigDecimal(dividend);
        BigDecimal b2 = new BigDecimal(divisor);
        return b1.remainder(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 比较大小
     *
     * @param value1 被比较数
     * @param value2 比较数
     * @return 如果v1 大于v2 则 返回true 否则false
     */
    public static boolean compare(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        int bj = b1.compareTo(b2);
        return bj > 0;
    }
}


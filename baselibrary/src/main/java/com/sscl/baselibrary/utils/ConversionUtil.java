package com.sscl.baselibrary.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DecimalFormat;

/**
 * 数据转换工具类
 *
 * @author pengh
 */
public class ConversionUtil {

    /*--------------------------------静态常量--------------------------------*/

    /**
     * 设备地址的长度
     */
    private static final int ADDRESS_BYTE_LENGTH = 6;

    /*--------------------------------静态方法--------------------------------*/

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    @NonNull
    @SuppressWarnings("unused")
    public static String strToHexStr(@NonNull String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;

        for (byte b : bs) {
            bit = (b & 0x0f0) >>> 4;
            sb.append(chars[bit]);
            bit = b & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 字符串转换成byte数组（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public static byte[] getByteArray(@NonNull String s, @Size(min = 0) int byteArrayLength) {
        return getByteArray(s, Charset.defaultCharset(), byteArrayLength);
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
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public static byte[] getByteArray(@NonNull String s, @NonNull String charsetName, @Size(min = 0) int byteArrayLength) throws UnsupportedCharsetException {
        Charset charset = Charset.forName(charsetName);
        return getByteArray(s, charset, byteArrayLength);
    }

    /**
     * 字符串转换成byte数组（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param charset         编码方式
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public static byte[] getByteArray(@NonNull String s, @NonNull Charset charset, @Size(min = 0) int byteArrayLength) {
        byte[] data;
        if (byteArrayLength > 0) {
            if (s.length() > byteArrayLength) {
                data = new byte[byteArrayLength];
                System.arraycopy(s.getBytes(charset), 0, data, 0, byteArrayLength);
            } else {
                data = s.getBytes(charset);
            }
        } else {
            data = s.getBytes(charset);
        }
        return data;
    }

    /**
     * 字符串转换成byte数组，自动判断中文简体语言环境，在中文简体下，自动以GBK方式转换（数组长度最长为byteArrayLength）
     *
     * @param s               要转换成byte[]的字符串
     * @param byteArrayLength 数组长度的最大值（数组长度超过该值会被截取，长度不足该值为数组原长度）
     * @return 转换后获得的byte[]
     */
    @SuppressWarnings("unused")
    @NonNull
    public static byte[] getByteArrayAutoGbk(@NonNull String s, int byteArrayLength) {
        if (Tool.isZhCn()) {
            return getByteArray(s, "GBK", byteArrayLength);
        } else {
            return getByteArray(s, byteArrayLength);
        }
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    @SuppressWarnings("unused")
    @NonNull
    public static String hexStrToStr(@NonNull String hexStr) {
        String str = "0123456789ABCDEF";
        char[] cache = hexStr.toCharArray();
        byte[] byteArray = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < byteArray.length; i++) {
            n = str.indexOf(cache[2 * i]) * 16;
            n += str.indexOf(cache[2 * i + 1]);
            byteArray[i] = (byte) (n & 0xff);
        }
        return new String(byteArray);
    }

    /**
     * byteArray转换成十六进制字符串
     *
     * @param byteArray byte数组
     * @return String 每个Byte值之间空格分隔
     */
    @NonNull
    public static String byteArrayToHexStr(@NonNull byte[] byteArray) {
        String stmp;
        StringBuilder sb = new StringBuilder();
        for (byte aByte : byteArray) {
            stmp = Integer.toHexString(aByte & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 将长整形转为byte数组
     *
     * @param value 长整形
     * @return byte数组
     */
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public static byte[] longToByteArray(long value) {
        int byteLength = 2;
        String hexString = Long.toHexString(value);
        int length = hexString.length();
        if (length % byteLength == 0) {
            byte[] byteArray = new byte[length / byteLength];
            for (int i = 0; i < byteArray.length; i++) {
                String cacheString = hexString.substring(i * byteLength, i * byteLength + 2);
                short cache = Short.parseShort(cacheString, 16);
                byteArray[i] = (byte) cache;
            }
            return byteArray;
        } else {
            byte[] byteArray = new byte[length / byteLength + 1];
            String substring = hexString.substring(0, 1);
            byteArray[0] = (byte) Short.parseShort(substring, 16);
            hexString = hexString.substring(1);
            for (int i = 0; i < byteArray.length - 1; i++) {
                String cacheString = hexString.substring(i * byteLength, i * byteLength + 2);
                short cache = Short.parseShort(cacheString, 16);
                byteArray[i + 1] = (byte) cache;
            }
            return byteArray;
        }
    }

    /**
     * 将long类型数（0~0x0000FFFFFFFFFFFF之间）转为6字节byte数组
     *
     * @param value 0~0x0000FFFFFFFFFFFF之间的long类型数
     * @return 6字节byte数组
     */
    @SuppressWarnings("unused")
    @NonNull
    @Size(6)
    public static byte[] longToByteArrayLength6(@Size(min = 0, max = 0x0000FFFFFFFFFFFFL) long value) {
        int byteArrayLength = 6;
        byte[] byteArray = longToByteArray(value);
        if (byteArray.length == byteArrayLength) {
            return byteArray;
        }
        byte[] result = new byte[byteArrayLength];
        System.arraycopy(byteArray, 0, result, 6 - byteArray.length, byteArray.length);
        return result;
    }

    /**
     * 将 long 类型数转为指定长度的 byte 数组
     *
     * @param value           long 类型的数据
     * @param byteArrayLength 指定的数组长度
     * @return byte 数组
     */
    public static byte[] longToByteArray(long value, @IntRange(from = 1, to = 8) int byteArrayLength) {
        byte[] bytes = longToByteArray(value);
        int length = bytes.length;
        if (length < byteArrayLength) {
            int index = byteArrayLength - length;
            byte[] cache = new byte[byteArrayLength];
            System.arraycopy(bytes, 0, cache, index, length);
            bytes = new byte[byteArrayLength];
            System.arraycopy(cache, 0, bytes, 0, byteArrayLength);
        } else if (length > byteArrayLength) {
            int index = length - byteArrayLength;
            byte[] cache = new byte[byteArrayLength];
            System.arraycopy(bytes, index, cache, 0, byteArrayLength);
            bytes = new byte[byteArrayLength];
            System.arraycopy(cache, 0, bytes, 0, cache.length);
        }
        return bytes;
    }

    /**
     * byteArray字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    @SuppressWarnings("unused")
    @NonNull
    public static byte[] hexStrToByteArray(@NonNull String src) throws NumberFormatException {
        int m, n;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int integer = Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
            ret[i] = (byte) integer;
        }
        return ret;
    }

    /**
     * byte数组转为long
     *
     * @param byteArray byte数组
     * @return long
     */
    public static long byteArrayToLong(@NonNull @Size(min = 1, max = 8) byte[] byteArray) {
        int length = byteArray.length;
        int maxLongByteArrayLength = 8;
        int byteBits = 8;
        if (byteArray.length == 0 || byteArray.length > maxLongByteArrayLength) {
            throw new RuntimeException("byteArray length must be in range 1 ~ 8");
        }

        long cache = 0;
        for (int i = 0; i < length; i++) {
            byte aByte = byteArray[i];
            int unsignedByte = getUnsignedByte(aByte);
            cache = cache | unsignedByte << (byteBits * (length - i - 1));
        }
        return cache;
    }


    /**
     * String的字符串转换成unicode的String
     *
     * @param strText 全角字符串
     * @return String 每个unicode之间无分隔符
     */
    @SuppressWarnings("unused")
    @NonNull
    public static String strToUnicode(@NonNull String strText) {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128) {
                str.append("\\u").append(strHex);
            }
            // 低位在前面补00
            else {
                str.append("\\u00").append(strHex);
            }
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    @SuppressWarnings("unused")
    @NonNull
    public static String unicodeToString(@NonNull String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * 将一个byte数组拼接为一个int型数
     *
     * @param byteArray byte数组长度不超过4
     * @return int型数
     */
    public static int byteArrayToInt(@NonNull @Size(min = 1, max = 4) byte[] byteArray) {
        int length = byteArray.length;
        int maxLongByteArrayLength = 4;
        int byteBits = 8;
        if (byteArray.length == 0 || byteArray.length > maxLongByteArrayLength) {
            throw new RuntimeException("byteArray length must be in range 1 ~ 4");
        }
        int cache = 0;
        for (int i = 0; i < length; i++) {
            byte aByte = byteArray[i];
            int unsignedByte = getUnsignedByte(aByte);
            cache = cache | unsignedByte << (byteBits * (length - i - 1));
        }
        return cache;
    }

    /**
     * 格式化数字为千分位显示；
     *
     * @param number 要格式化的数字
     * @return
     */
    public static String formatMicrometer(String number) {
        DecimalFormat df;
        String point = ".";
        if (number.indexOf(point) > 0) {
            int index = number.length() - number.indexOf(point) - 1;
            if (index == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (index == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double result;
        try {
            result = Double.parseDouble(number);
        } catch (Exception e) {
            result = 0.0;
        }
        return df.format(result);
    }

    /**
     * 将一个整数转换成2个字节的byte数组
     *
     * @param i 整数
     * @return 2个字节的byte数组
     */
    @SuppressWarnings("unused")
    @Size(2)
    @NonNull
    public static byte[] intToByteArrayLength2(int i) {
        String hexString = intToHexStr(i);
        byte highByte;
        byte lowByte;
        int hexStringMinLength = 2;
        if (hexString.length() > hexStringMinLength) {
            String substring = hexString.substring(0, hexString.length() - 2);
            highByte = (byte) Integer.parseInt(substring, 16);
            substring = hexString.substring(hexString.length() - 2);
            lowByte = (byte) Integer.parseInt(substring, 16);
        } else {
            highByte = 0;
            lowByte = (byte) Integer.parseInt(hexString, 16);
        }
        return new byte[]{highByte, lowByte};
    }

    /**
     * 将一个整数转换成4个字节的byte数组
     *
     * @param n 整数
     * @return 4个字节的byte数组
     */
    @SuppressWarnings("unused")
    @Size(4)
    @NonNull
    public static byte[] intToByteArrayLength4(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte) (n >>> 24 - i * 8);
        }
        return b;
    }

    /**
     * 将整数转换成16进制字符串
     *
     * @param i 整数
     * @return 16进制字符串
     */
    @NonNull
    @SuppressWarnings("WeakerAccess")
    public static String intToHexStr(int i) {
        return Integer.toHexString(i);
    }

    /**
     * 将字节型数据转换为0~255 (0xFF 即BYTE)
     *
     * @param data data字节型数据
     * @return 无符号的整型
     */
    public static int getUnsignedByte(byte data) {
        return data & 0x0FF;
    }

    /**
     * 将字节型数据转换为0~65535 (0xFFFF 即 WORD)
     *
     * @param data 字节型数据
     * @return 无符号的整型
     */
    @SuppressWarnings("unused")
    public static int getUnsignedShort(short data) {
        return data & 0x0FFFF;
    }

    /**
     * 将带空格的十六进制字符串转为byte数组
     *
     * @param hexStr 带空格的十六进制字符串
     * @return byte数组
     */
    @SuppressWarnings("unused")
    @Nullable
    public static byte[] hexStringToByteArray(@NonNull String hexStr) {
        //将获取到的数据以空格为间隔截取成数组
        String[] split = hexStr.split(" ");
        int length = split.length;

        //新建一个byte数组
        byte[] result = new byte[length];

        int cache;
        //用循环使截取出来的16进制字符串数组中的字符串转为byte,并固定for循环次数为定义的长度(这样的话，即使String数组长度大于定义的长度，也不会发生数组越界异常)
        for (int i = 0; i < result.length; i++) {
            try {
                cache = Integer.parseInt(split[i], 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
            result[i] = (byte) cache;
        }
        return result;
    }

    /**
     * 将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
     *
     * @param data int数据
     * @return 无符号的长整型
     */
    public static long getUnsignedInt(int data) {
        //获取最低位
        int lowBit = (byte) (0b1 & data);
        //无符号右移一位（无符号数）
        int i = data >>> 1;
        //将右移之后的数强转为long之后重新左移回去
        long l = (long) i << 1;
        //重新加上低位的值
        return l + lowBit;
    }

    /**
     * 将int转为boolean(0 = false ,1 = true)
     *
     * @param value int值
     *              *
     * @return 对应的结果
     */
    @SuppressWarnings("unused")
    public static boolean intToBoolean(int value) {
        switch (value) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new RuntimeException("The error value " + value);
        }
    }

    /**
     * 将boolean转为int(true = 1,false = 0)
     *
     * @param b boolean值
     *          *
     * @return 对应的int值
     */
    @SuppressWarnings("unused")
    public static int booleanToInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 使用代码触发home键的效果
     *
     * @param context 上下文
     */
    @SuppressWarnings("unused")
    public static void pressHomeButton(@NonNull Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        // 注意:必须加上这句代码，否则就不是单例了
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    /**
     * 检测byte数组中的内容有效性（全0为无效）
     *
     * @param byteArray byte数组
     * @return true表示有效
     */
    @SuppressWarnings("unused")
    public static boolean checkByteValid(@NonNull byte[] byteArray) {
        for (byte aByte : byteArray) {
            if (aByte != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将任意对象转为byte数组
     *
     * @param o 任意对象
     * @return byte数组
     */
    @SuppressWarnings("unused")
    @Nullable
    public static byte[] objectToByteArray(@NonNull Object o) {
        byte[] byteArray = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            byteArray = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 将数组类型转为指定的对象
     *
     * @param byteArray 数组类
     * @return T 指定对象
     */
    @SuppressWarnings("unused")
    @Nullable
    public static <T extends Serializable> T byteArrayToObject(@NonNull byte[] byteArray) {
        Object o = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            o = objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //noinspection unchecked
        return (T) o;
    }

    /**
     * 将蓝牙设备地址转为byte数组
     *
     * @param address 设备地址
     * @return byte数组
     */
    @SuppressWarnings("unused")
    @Nullable
    public static byte[] bluetoothAddressStringToByteArray(@NonNull String address) {

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return null;
        }

        String[] cacheArray = address.split(":", 6);
        byte[] bluetoothByteArray = new byte[6];

        for (int i = 0; i < cacheArray.length; i++) {
            String cache = cacheArray[i];
            Integer integer;
            try {
                integer = Integer.valueOf(cache, 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
            bluetoothByteArray[i] = integer.byteValue();
        }
        return bluetoothByteArray;
    }

    /**
     * 将设备地址数组转为设备地址字符串
     *
     * @param addressByteArray 设备地址数组
     * @return 设备地址字符串（AA:AA:AA:AA:AA:AA）
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String bluetoothAddressByteArrayToString(@NonNull byte[] addressByteArray) {
        if (addressByteArray.length != ADDRESS_BYTE_LENGTH) {
            return null;
        }

        String addressCacheString = byteArrayToHexStr(addressByteArray);
        String addressCache = addressCacheString.replace(" ", ":");
        return addressCache.toUpperCase();
    }

    /**
     * 将一个int型的Ip地址转为点分式地址字符串
     *
     * @param ip int型的Ip地址
     * @return 点分式字符串
     */
    @SuppressWarnings("unused")
    @NonNull
    public static String intIp4ToStringIp4(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }

    /**
     * 将一个int型的Ip地址转为点分式地址字符串
     *
     * @param ip int型的Ip地址
     * @return 点分式字符串
     */
    @SuppressWarnings("unused")
    @NonNull
    public static String intIp4ToReverseStringIp4(int ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }
}

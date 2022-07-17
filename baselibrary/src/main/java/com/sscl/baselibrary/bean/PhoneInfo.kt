package com.sscl.baselibrary.bean

/**
 * 手机信息Bean类
 *
 * @author pengh
 */
class PhoneInfo
/**
 * 构造方法
 *
 * @param manufacturer 手机厂商
 * @param model        手机型号
 * @param deviceId     设备Id
 */(
    /**
     * 手机厂商
     */
    private val manufacturer: String?,
    /**
     * 手机型号
     */
    private val model: String?,
    /**
     * 设备Id
     */
    private val deviceId: String?
) {

    override fun toString(): String {
        return "PhoneInfo{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}'
    }
}
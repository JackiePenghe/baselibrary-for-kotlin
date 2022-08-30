package com.sscl.baselibrary.bean

/**
 * 设备基本信息Bean类
 *
 * @author pengh
 */
@Suppress("MemberVisibilityCanBePrivate")
class DeviceInfo(
    /**
     * 手机厂商
     */
    val manufacturer: String?,
    /**
     * 手机型号
     */
    val model: String?,
    /**
     * 设备Id
     */
    val deviceId: String?
) {

    override fun toString(): String {
        return "PhoneInfo{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}'
    }
}
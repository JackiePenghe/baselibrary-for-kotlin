package com.sscl.baselibrary.bean;

import androidx.annotation.Nullable;

/**
 * 手机信息Bean类
 *
 * @author pengh
 */
public class PhoneInfo {

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 手机厂商
     */
    private final String manufacturer;
    /**
     * 手机型号
     */
    private final String model;
    /**
     * 设备Id
     */
    private final String deviceId;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param manufacturer 手机厂商
     * @param model        手机型号
     * @param deviceId     设备Id
     */
    public PhoneInfo(@Nullable String manufacturer, @Nullable String model, @Nullable String deviceId) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.deviceId = deviceId;
    }

    /*--------------------------------getter--------------------------------*/

    @SuppressWarnings("unused")
    @Nullable
    public String getManufacturer() {
        return manufacturer;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getModel() {
        return model;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String toString() {
        return "PhoneInfo{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}

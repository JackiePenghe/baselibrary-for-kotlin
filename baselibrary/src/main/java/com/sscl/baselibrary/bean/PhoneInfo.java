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
    private String manufacturer;
    /**
     * 手机型号
     */
    private String model;
    /**
     * 手机IMEI
     */
    private String phoneImei;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param manufacturer 手机厂商
     * @param model        手机型号
     * @param phoneImei    手机IMEI
     */
    public PhoneInfo(@Nullable String manufacturer,@Nullable String model, @Nullable String phoneImei) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.phoneImei = phoneImei;
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
    public String getPhoneImei() {
        return phoneImei;
    }
}

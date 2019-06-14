package com.sscl.baselibrary.bean;

/**
 * 手机信息Bean类
 *
 * @author pengh
 */
public class PhoneInfo {

    /*--------------------------------成员变量--------------------------------*/

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
    public PhoneInfo(String manufacturer, String model, String phoneImei) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.phoneImei = phoneImei;
    }

    /*--------------------------------getter--------------------------------*/

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getPhoneImei() {
        return phoneImei;
    }
}

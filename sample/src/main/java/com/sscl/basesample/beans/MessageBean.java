package com.sscl.basesample.beans;

/**
 * @author LiangYaLong
 * 描述:      mqtt消息实体类
 * 时间:     2021/3/24
 * 版本:     1.0
 */
public class MessageBean<T> {
    private int cmd;
    private T data;
    private String deviceId;
    private String trade_no;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
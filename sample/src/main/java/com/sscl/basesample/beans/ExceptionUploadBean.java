package com.sscl.basesample.beans;

import java.util.List;

public class ExceptionUploadBean {

    /**
     * code : 200
     * data : []
     * msg : 成功
     */

    private Integer code;
    private String msg;
    private List<?> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
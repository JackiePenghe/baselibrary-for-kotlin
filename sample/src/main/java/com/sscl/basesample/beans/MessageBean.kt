package com.sscl.basesample.beans

/**
 * @author LiangYaLong
 * 描述:      mqtt消息实体类
 * 时间:     2021/3/24
 * 版本:     1.0
 */
open class MessageBean<T> {
    var cmd = 0
    var data: T? = null
        private set
    var deviceId: String? = null
    var trade_no: String? = null
    fun setData(data: T) {
        this.data = data
    }
}
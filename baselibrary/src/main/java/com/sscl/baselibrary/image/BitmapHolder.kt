package com.sscl.baselibrary.image

import android.widget.ImageView

/**
 * Created by alm on 17-6-6.
 * 用来封装Url和ImageView，防止图片错位里用
 */
class BitmapHolder
/**
 * 构造方法
 *
 * @param url 网络图片url
 * @param imageView 图片显示控件
 */(
    /**
     * 网络图片url
     */
    var url: String,
    /**
     * 图片显示控件
     */
    var imageView: ImageView
) {
}
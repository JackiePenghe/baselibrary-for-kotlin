package com.sscl.baselibrary.image;

import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * Created by alm on 17-6-6.
 * 用来封装Url和ImageView，防止图片错位里用
 */

class BitmapHolder {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 网络图片url
     */
    String url;

    /**
     * 图片显示控件
     */
    ImageView imageView;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param url 网络图片url
     * @param imageView 图片显示控件
     */
    BitmapHolder(@NonNull String url,@NonNull ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }
}

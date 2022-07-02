package com.sscl.baselibrary.widget.bannernew;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.widget.bannernew.enums.BannerDataType;
import com.sscl.baselibrary.widget.bannernew.enums.BannerType;

import java.io.File;

/**
 * Banner数据类
 *
 * @author pengh
 */
public interface BannerData<T> {

    /**
     * 获取Banner类型
     *
     * @return Banner类型
     */
    @NonNull
    BannerType getBannerType();

    /**
     * 获取Banner数据类型
     *
     * @return Banner数据类型
     */
    @NonNull
    BannerDataType getBannerDataType();

    /**
     * 获取文件数据
     *
     * @return 文件
     */
    @Nullable
    File getFileData();

    /**
     * 获取网络URL数据
     *
     * @return 网络URL
     */
    @Nullable
    String getUrlData();

    /**
     * 获取本地URI数据
     *
     * @return 本地URI
     */
    @Nullable
    Uri getUriData();

    /**
     * 获取自定义数据
     *
     * @return 自定义数据
     */
    @Nullable
    T getCustomData();
}

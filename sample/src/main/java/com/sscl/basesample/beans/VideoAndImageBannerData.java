package com.sscl.basesample.beans;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.widget.banner.news.BannerData;
import com.sscl.baselibrary.widget.banner.news.enums.BannerDataType;
import com.sscl.baselibrary.widget.banner.news.enums.BannerType;

import java.io.File;

/**
 * @author pengh
 */
public class VideoAndImageBannerData implements BannerData<Object> {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 文件
     */
    private final File file;
    /**
     * Banner类型
     */
    private final BannerType bannerType;



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 构造方法
     *
     * @param file 文件
     */
    public VideoAndImageBannerData(@NonNull BannerType bannerType, @NonNull File file) {
        this.file = file;
        this.bannerType = bannerType;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 获取Banner类型
     *
     * @return Banner类型
     */
    @NonNull
    @Override
    public BannerType getBannerType() {
        return bannerType;
    }

    /**
     * 获取Banner数据类型
     *
     * @return Banner数据类型
     */
    @NonNull
    @Override
    public BannerDataType getBannerDataType() {
        return BannerDataType.FILE;
    }

    /**
     * 获取文件数据
     *
     * @return 文件
     */
    @Nullable
    @Override
    public File getFileData() {
        return file;
    }

    /**
     * 获取网络URL数据
     *
     * @return 网络URL
     */
    @Nullable
    @Override
    public String getUrlData() {
        return null;
    }

    /**
     * 获取本地URI数据
     *
     * @return 本地URI
     */
    @Nullable
    @Override
    public Uri getUriData() {
        return null;
    }

    /**
     * 获取自定义数据
     *
     * @return 自定义数据
     */
    @Nullable
    @Override
    public Object getCustomData() {
        return null;
    }
}

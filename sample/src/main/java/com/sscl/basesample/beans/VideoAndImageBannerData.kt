package com.sscl.basesample.beans

import com.sscl.baselibrary.widget.banner.enums.BannerType
import com.sscl.baselibrary.widget.banner.BannerData
import com.sscl.baselibrary.widget.banner.enums.BannerDataType
import android.net.Uri
import java.io.File

/**
 * @author pengh
 */
class VideoAndImageBannerData
/**
 * 构造方法
 *
 * @param file 文件
 */(
    /**
     * Banner类型
     */
    override val bannerType: BannerType,
    /**
     * 文件
     */
    private val file: File
) : BannerData<Any?> {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 获取Banner类型
     *
     * @return Banner类型
     */
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 获取Banner数据类型
     *
     * @return Banner数据类型
     */
    override val bannerDataType: BannerDataType
        get() = BannerDataType.FILE

    /**
     * 获取文件数据
     *
     * @return 文件
     */
    override val fileData: File?
        get() = file

    /**
     * 获取网络URL数据
     *
     * @return 网络URL
     */
    override val urlData: String?
        get() = null

    /**
     * 获取本地URI数据
     *
     * @return 本地URI
     */
    override val uriData: Uri?
        get() = null

    /**
     * 获取自定义数据
     *
     * @return 自定义数据
     */
    override val customData: Any?
        get() = null
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
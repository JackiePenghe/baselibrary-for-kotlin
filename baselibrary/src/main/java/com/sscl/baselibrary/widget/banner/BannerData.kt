package com.sscl.baselibrary.widget.banner

import android.net.Uri
import com.sscl.baselibrary.widget.banner.enums.BannerDataType
import com.sscl.baselibrary.widget.banner.enums.BannerType
import java.io.File

/**
 * Banner数据类
 *
 * @author pengh
 */
interface BannerData<T> {
    /**
     * 获取Banner类型
     *
     * @return Banner类型
     */
    val bannerType: BannerType

    /**
     * 获取Banner数据类型
     *
     * @return Banner数据类型
     */
    val bannerDataType: BannerDataType

    /**
     * 获取文件数据
     *
     * @return 文件
     */
    val fileData: File?

    /**
     * 获取网络URL数据
     *
     * @return 网络URL
     */
    val urlData: String?

    /**
     * 获取本地URI数据
     *
     * @return 本地URI
     */
    val uriData: Uri?

    /**
     * 获取自定义数据
     *
     * @return 自定义数据
     */
    val customData: T?
}
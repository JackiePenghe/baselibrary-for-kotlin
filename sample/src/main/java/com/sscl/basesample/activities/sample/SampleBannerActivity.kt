package com.sscl.basesample.activities.sample

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.sscl.baselibrary.files.FileUtil
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.widget.banner.Banner
import com.sscl.baselibrary.widget.banner.Banner.OnCustomDataHandleListener
import com.sscl.baselibrary.widget.banner.BannerData
import com.sscl.baselibrary.widget.banner.OnItemClickListener
import com.sscl.baselibrary.widget.banner.enums.BannerDataType
import com.sscl.baselibrary.widget.banner.enums.BannerType
import com.sscl.basesample.R
import com.sscl.basesample.beans.VideoAndImageBannerData
import java.io.File

/**
 * 新Banner的使用
 *
 * @author pengh
 */
class SampleBannerActivity : AppCompatActivity() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Banner
     */
    private lateinit var banner: Banner

    private val TAG:String = SampleBannerActivity::class.java.simpleName

    /**
     * 自定义的Banner数据处理回调接口
     */
    private val onCustomDataHandleListener: OnCustomDataHandleListener =
        object : OnCustomDataHandleListener {
            /**
             * 获取用户自定义数据的View（需要重复播放时的View，仅当只有一个数据时回调此方法）
             *
             *
             * @param context 上下文
             * @param bannerData 用户自定义数据
             * @return 用户自定义数据的View
             */
            override fun getAutoPlayRepeatItemView(
                context: Context,
                bannerData: BannerData<*>
            ): View {
                val customData = bannerData.customData as String?
                val textView = TextView(context)
                textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                textView.gravity = Gravity.CENTER
                textView.setBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
                textView.textSize = 40f
                textView.text = customData
                return textView
            }

            /**
             * 获取用户自定义数据的View
             *
             * @param context    上下文
             * @param bannerData 用户自定义数据
             * @param position   当前数据的位置
             * @return 用户自定义数据的View
             */
            override fun getItemView(
                context: Context,
                bannerData: BannerData<*>,
                position: Int
            ): View {
                val customData = bannerData.customData as String?
                val textView = TextView(context)
                textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                textView.gravity = Gravity.CENTER
                textView.setBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
                textView.textSize = 40f
                textView.text = customData
                return textView
            }

            /**
             * 让用户设置自定义数据的VideoView数据(设置完数据的VideoView会在显示时自动播放)
             *
             * @param context    上下文
             * @param videoView  视频播放控件
             * @param bannerData 用户自定义数据
             */
            override fun setVideoViewData(
                context: Context,
                videoView: VideoView,
                bannerData: BannerData<*>
            ) {
            }

            /**
             * 让用户设置自定义数据的ImageView数据
             *
             * @param context    上下文
             * @param imageView  图片播放控件
             * @param bannerData 用户自定义数据
             */
            override fun setImageViewData(
                context: Context,
                imageView: ImageView,
                bannerData: BannerData<*>
            ) {
            }

            /**
             * 让用户设置自定义数据滚动延时
             *
             * @param view              当前显示的View
             * @param bannerData        用户自定义数据
             * @param position          当前数据的位置
             * @param defaultScrollTime 默认的滚动延时
             * @return 返回滚动延时
             */
            override fun onGetDelayTime(
                view: View,
                bannerData: BannerData<*>,
                position: Int,
                defaultScrollTime: Long
            ): Long {
                return defaultScrollTime
            }
        }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_sscl_basesample_activity_sample_new_banner)
        banner = findViewById(R.id.banner)
        //允许手动滚动
        banner.isEnableSlide = true
        //设置图片缩放
        banner.setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
        //设置是否开启图片压缩
        banner.setNeedCompressImage(true)
        //设置是否按指定大小压缩
        banner.setCompressPictureWithMaxSize(true)
        //设置图片压缩的最大值，200KB
        banner.setMaxCompressPictureSize(200)
        banner.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                DebugUtil.warnOut(TAG, "onItemClick position:$position")
            }
        })
        //默认获取SD卡的根目录下”advertiseDir“文件夹下的图片与视频，没有则使用getDefaultBannerData()获取默认数据
        val videoAndImageBannerData = initBannerData()
        if (videoAndImageBannerData != null) {
            banner.setDataList(videoAndImageBannerData)
        } else {
            banner.setOnCustomDataHandleListener(onCustomDataHandleListener)
            banner.setDataList(defaultBannerData)
        }
    }

    private val defaultBannerData: ArrayList<BannerData<String>>
        get() {
            val bannerData = ArrayList<BannerData<String>>()
            for (i in 0..4) {
                bannerData.add(object : BannerData<String> {
                    override val bannerType: BannerType
                        get() = BannerType.CUSTOM
                    override val bannerDataType: BannerDataType
                        get() = BannerDataType.CUSTOM
                    override val fileData: File?
                        get() = null
                    override val urlData: String?
                        get() = null
                    override val uriData: Uri?
                        get() = null
                    override val customData: String
                        get() = "自定义Banner数据$i"
                })
            }
            return bannerData
        }

    /**
     * {@inheritDoc}
     *
     *
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        if (!banner.isStart) {
            banner.startBanner()
        }
        banner.startAutoScroll()
    }

    /**
     * {@inheritDoc}
     *
     *
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        super.onPause()
        banner.stopAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        banner.destroy()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private fun initBannerData(): ArrayList<VideoAndImageBannerData>? {
        val videoAndImageBannerData = ArrayList<VideoAndImageBannerData>()
        val dir = File(FileUtil.sdCardAppDir, "advertiseDir")
        if (dir.exists()) {
            if (dir.isFile) {
                dir.delete()
            }
        } else {
            dir.mkdirs()
        }
        val files = dir.listFiles()
        if (files == null || files.isEmpty()) {
            return null
        }
        for (file in files) {
            if (!file.isFile) {
                continue
            }
            if (!isMp4File(file)) {
                videoAndImageBannerData.add(VideoAndImageBannerData(BannerType.IMAGE, file))
            } else {
                videoAndImageBannerData.add(VideoAndImageBannerData(BannerType.VIDEO, file))
            }
        }
        return videoAndImageBannerData
    }

    /**
     * 判断是否为MP4文件
     *
     * @param file 文件
     * @return 是否为MP4文件
     */
    private fun isMp4File(file: File): Boolean {
        return file.name.endsWith("mp4")
    }
}
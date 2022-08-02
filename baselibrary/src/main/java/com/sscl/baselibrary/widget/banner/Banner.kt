package com.sscl.baselibrary.widget.banner

import android.content.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.*
import android.widget.ImageView.ScaleType
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sscl.baselibrary.files.FileProviderUtil
import com.sscl.baselibrary.utils.BaseManager
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.widget.banner.enums.BannerDataType
import com.sscl.baselibrary.widget.banner.enums.BannerType
import java.io.File
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author pengh
 */
class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {
        private val TAG: String = Banner::class.java.simpleName
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 用户自定义数据处理接口
     */
    interface OnCustomDataHandleListener {
        /**
         * 获取用户自定义数据的View（需要重复播放时的View，仅当只有一个数据时回调此方法）
         *
         * @param context    上下文
         * @param bannerData 用户自定义数据
         * @return 用户自定义数据的View
         */
        fun getAutoPlayRepeatItemView(context: Context, bannerData: BannerData<*>): View

        /**
         * 获取用户自定义数据的View
         *
         * @param context    上下文
         * @param bannerData 用户自定义数据
         * @param position   当前数据的位置
         * @return 用户自定义数据的View
         */
        fun getItemView(context: Context, bannerData: BannerData<*>, position: Int): View

        /**
         * 让用户设置自定义数据的VideoView数据(设置完数据的VideoView会在显示时自动播放)
         *
         * @param context    上下文
         * @param videoView  视频播放控件
         * @param bannerData 用户自定义数据
         */
        fun setVideoViewData(context: Context, videoView: VideoView, bannerData: BannerData<*>)

        /**
         * 让用户设置自定义数据的ImageView数据
         *
         * @param context    上下文
         * @param imageView  图片播放控件
         * @param bannerData 用户自定义数据
         */
        fun setImageViewData(context: Context, imageView: ImageView, bannerData: BannerData<*>)

        /**
         * 让用户设置自定义数据滚动延时
         *
         * @param view              当前显示的View
         * @param bannerData        用户自定义数据
         * @param position          当前数据的位置
         * @param defaultScrollTime 默认的滚动延时
         * @return 返回滚动延时
         */
        fun onGetDelayTime(
            view: View,
            bannerData: BannerData<*>,
            position: Int,
            defaultScrollTime: Long
        ): Long
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 是否开启自动轮播，默认为false
     */
    private var enableAutoScroll: Boolean = false

    /**
     * 自动轮播的时间间隔（非视频类型的时候应用此时间）
     */
    private var autoScrollTime: Long = 3000

    /**
     * 实际应用的自动轮播的时间间隔（非视频类自动应用[Banner.autoScrollTime],视频类则使用视频文件的播放时长，自定义类型则需要用户手动处理并返回）
     */
    private var delayedTime: Long = 0

    /**
     * 记录上一次选择的位置
     */
    private var lastPosition: Int = -1

    /**
     * 当前显示位置
     */
    private var currentPosition: Int = 0

    /**
     * 自动轮播的定时器
     */
    private var autoScrollTimer: ScheduledExecutorService? = null

    /**
     * Banner是否已经开启
     */
    var isStart: Boolean = false
        private set

    /**
     * 用户自定义数据处理接口
     */
    private var onCustomDataHandleListener: OnCustomDataHandleListener? = null

    /**
     * 图片默认的ScaleType
     */
    private var imageScaleType: ScaleType = ScaleType.CENTER_CROP

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Banner ViewPager
     */
    private var viewPager: BannerViewPager? = null

    /**
     * Banner适配器
     */
    private val adapter: BannerAdapter = BannerAdapter()

    /**
     * banner数据缓存
     */
    private val bannerDataList: ArrayList<BannerData<*>> = ArrayList()

    /**
     * ViewPager滑动监听
     */
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            lastPosition = currentPosition
            currentPosition = position
            calculateCurrentDelayTime(position)
            if (enableAutoScroll) {
                startAutoScrollTimer()
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (enableAutoScroll && state != ViewPager.SCROLL_STATE_IDLE) {
                stopAutoScrollTimer()
            }
            //ViewPager跳转
            var pageIndex: Int = currentPosition
            if (currentPosition == 0) {
                pageIndex = adapter.views.size - 2
            } else if (currentPosition == adapter.views.size - 1) {
                pageIndex = 1
            }
            if (pageIndex != currentPosition) {
                //无滑动动画，直接跳转
                viewPager?.setCurrentItem(pageIndex, false)
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    init {
        viewPager = BannerViewPager(context)
        val params: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        viewPager?.layoutParams = params
        addView(viewPager)
        viewPager?.offscreenPageLimit = 0
        viewPager?.addOnPageChangeListener(onPageChangeListener)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 设置是否允许滑动
     *
     */
    var isEnableSlide: Boolean
        get() {
            return viewPager?.isEnableSlide == true
        }
        set(enableSlide) {
            viewPager?.isEnableSlide = enableSlide
        }

    /**
     * 设置自定义数据处理接口
     *
     * @param onCustomDataHandleListener 自定义数据处理接口
     */
    fun setOnCustomDataHandleListener(onCustomDataHandleListener: OnCustomDataHandleListener?) {
        this.onCustomDataHandleListener = onCustomDataHandleListener
    }

    /**
     * 设置自动轮播的时间间隔（非视频类型的时候应用此时间）
     *
     * @param autoScrollTime 自动轮播的时间间隔（非视频类型的时候应用此时间）
     */
    fun setAutoScrollTime(autoScrollTime: Long) {
        this.autoScrollTime = autoScrollTime
    }

    /**
     * @param dataList 轮播数组
     * @param <K>      泛型
     * @param <T>      泛型
    </T></K> */
    fun <T, K : BannerData<T>> setDataList(dataList: ArrayList<K>) {
        setDataList(dataList, -1)
    }

    /**
     * @param dataList 轮播数组
     * @param index    索引
     * @param <T>      泛型
     * @param <K>      泛型
    </K></T> */
    fun <T, K : BannerData<T>> setDataList(dataList: ArrayList<K>, index: Int) {
        bannerDataList.clear()
        if (index >= 0) {
            lastPosition = -1
            currentPosition = index
        }
        adapter.clear()
        if (dataList.size > 1) {
            handleDataListSizeBiggerThan1(dataList)
        } else if (dataList.size == 1) {
            handleDataListSizeEqualTo1(dataList)
        } else {
            handleDataListSizeEqualTo0()
        }
        adapter.notifyDataSetChanged()
    }

    fun startBanner() {
        if (isStart) {
            return
        }
        isStart = true
        viewPager?.adapter = adapter
    }

    /**
     * 开启自动轮播
     */
    fun startAutoScroll() {
        enableAutoScroll = true
        viewPager?.currentItem = currentPosition
    }

    /**
     * 停止自动轮播
     */
    fun stopAutoScroll() {
        enableAutoScroll = false
        stopAutoScrollTimer()
    }

    /**
     * 设置图片的缩放模式
     *
     * @param imageScaleType 图片的缩放模式
     */
    fun setImageScaleType(imageScaleType: ScaleType) {
        this.imageScaleType = imageScaleType
    }

    /**
     * 销毁
     */
    fun destroy() {
        stopAutoScroll()
        viewPager?.adapter = null
        bannerDataList.clear()
        adapter.clear()
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 获取delyedTime
     *
     * @param position 当前位置
     */
    private fun calculateCurrentDelayTime(position: Int) {
        if (lastPosition >= 0 && adapter.views.isNotEmpty() && adapter.views.size > lastPosition) {
            //noinspection rawtypes
            val bannerData = bannerDataList[lastPosition]
            if (bannerData.bannerType == BannerType.VIDEO) {
                val view = adapter.views[lastPosition] as RelativeLayout
                val videoView = view.getChildAt(0) as VideoView
                videoView.pause()
            }
        }
        //noinspection rawtypes
        val bannerData = bannerDataList[position]
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (bannerData.bannerType) {
            BannerType.IMAGE -> {
                delayedTime = autoScrollTime
            }
            BannerType.VIDEO -> {
                val view = adapter.views[position] as RelativeLayout
                val videoView = view.getChildAt(0) as VideoView
                delayedTime = Long.MAX_VALUE
                videoView.setOnCompletionListener {
                    viewPager?.setCurrentItem(position + 1, true)
                }
                videoView.setOnErrorListener { _, what, extra ->
                    DebugUtil.errorOut(TAG, "VideoView error: $what $extra")
                    viewPager?.setCurrentItem(currentPosition + 1, true)
                    true
                }
                videoView.seekTo(0)
                videoView.start()
            }
            BannerType.CUSTOM -> {
                delayedTime = onCustomDataHandleListener?.onGetDelayTime(
                    adapter.views[position],
                    bannerData,
                    position,
                    autoScrollTime
                ) ?: throw RuntimeException("请先设置onCustomDataHandleListener接口")

            }
            //所有case都已经处理，else不会执行
            else -> {
                DebugUtil.warnOut(TAG, "未处理的Banner类型")
                delayedTime = autoScrollTime
            }
        }
    }

    /**
     * 处理数据
     *
     * @param dataList 数据列表
     * @param <T>      泛型
     * @param <K>      泛型
    </K></T> */
    private fun <T, K : BannerData<T>> handleDataListSizeBiggerThan1(dataList: ArrayList<K>) {
        lastPosition = -1
        currentPosition = 1
        for (i in 0 until dataList.size + 2) {
            when (i) {
                0 -> {
                    bannerDataList.add(dataList[dataList.size - 1])
                }
                dataList.size + 1 -> {
                    bannerDataList.add(dataList[0])
                }
                else -> {
                    bannerDataList.add(dataList[i - 1])
                }
            }
        }
        for (i in bannerDataList.indices) {
            val bannerData: BannerData<*> = bannerDataList[i]
            val layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            when (bannerData.bannerType) {
                BannerType.IMAGE -> {
                    val imageView = ImageView(context)
                    imageView.layoutParams = layoutParams
                    imageView.scaleType = imageScaleType
                    handleImageBannerData(imageView, bannerData)
                    adapter.views.add(imageView)
                }
                BannerType.VIDEO -> {
                    val relativeLayout = RelativeLayout(context)
                    relativeLayout.layoutParams = layoutParams
                    val videoView = VideoView(context)
                    videoView.layoutParams = layoutParams
                    videoView.setOnErrorListener { _, _, _ -> true }
                    handleVideoBannerData(videoView, bannerData)
                    relativeLayout.addView(videoView)
                    val layoutParamsRelativeLayout: RelativeLayout.LayoutParams =
                        videoView.layoutParams as RelativeLayout.LayoutParams
                    layoutParamsRelativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT)
                    videoView.layoutParams = layoutParamsRelativeLayout
                    adapter.views.add(relativeLayout)
                }
                BannerType.CUSTOM -> {
                    val itemView: View =
                        onCustomDataHandleListener?.getItemView(context, bannerData, i)
                            ?: throw RuntimeException("请设置OnCustomDataHandleListener接口")
                    adapter.views.add(itemView)
                }
                else -> DebugUtil.warnOut(TAG, "未处理的Banner类型")
            }
        }
    }

    /**
     * 处理Banner数据
     *
     * @param dataList 数据列表
     * @param <K>      泛型
    </K> */
    private fun <T, K : BannerData<T>> handleDataListSizeEqualTo1(dataList: ArrayList<K>) {
        lastPosition = -1
        currentPosition = 0
        bannerDataList.addAll(dataList)
        val bannerData: BannerData<*> = bannerDataList[0]
        val bannerType: BannerType = bannerData.bannerType
        val layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (bannerType) {
            BannerType.IMAGE -> {
                val imageView = ImageView(context)
                imageView.layoutParams = layoutParams
                imageView.scaleType = imageScaleType
                handleImageBannerData(imageView, bannerData)
                adapter.views.add(imageView)
            }
            BannerType.VIDEO -> {
                val relativeLayout = RelativeLayout(context)
                relativeLayout.layoutParams = layoutParams
                val videoView = VideoView(context)
                videoView.layoutParams = layoutParams
                videoView.setOnErrorListener { _, _, _ -> true }
                handleVideoBannerData(videoView, bannerData)
                //监听视频播放完的代码
                videoView.setOnCompletionListener {
                    it.start()
                    it.isLooping = true
                }
                videoView.setOnErrorListener { _, _, _ ->
                    DebugUtil.warnOut(TAG, "视频播放错误")
                    true
                }
                relativeLayout.addView(videoView)
                val layoutParamsRelativeLayout =  videoView.layoutParams as RelativeLayout.LayoutParams
                layoutParamsRelativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT)
                videoView.layoutParams = layoutParamsRelativeLayout
                adapter.views.add(relativeLayout)
                videoView.start()
            }
            BannerType.CUSTOM -> {
                val view: View =
                    onCustomDataHandleListener?.getAutoPlayRepeatItemView(
                        context,
                        bannerData
                    ) ?: throw RuntimeException("请设置OnCustomDataHandleListener接口")
                adapter.views.add(view)
            }
            else -> DebugUtil.warnOut(TAG, "未处理的Banner类型")
        }
    }

    /**
     * 处理Banner数据
     */
    private fun handleDataListSizeEqualTo0() {
        //添加一个默认的View
        adapter.views.add(LinearLayout(context))
    }

    /**
     * 处理视频数据
     *
     * @param videoView  视频视图
     * @param bannerData Banner数据
     */
    private fun handleVideoBannerData(videoView: VideoView, bannerData: BannerData<*>) {
        val bannerDataType: BannerDataType = bannerData.bannerDataType
        val onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus: Boolean ->
                if (!hasFocus) {
//                    videoView.start()
//                    videoView.seekTo(0)
//                } else {
                    videoView.pause()
//                }
                }
            }
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (bannerDataType) {
            BannerDataType.FILE -> {
                val fileData: File? = bannerData.fileData
                if (fileData == null) {
                    DebugUtil.warnOut(TAG, "视频文件为空")
                    return
                }
                videoView.setVideoURI(FileProviderUtil.getUriFromFile(context, fileData))
                videoView.onFocusChangeListener = onFocusChangeListener
            }
            BannerDataType.URI -> {
                val uriData: Uri? = bannerData.uriData
                if (uriData == null) {
                    DebugUtil.warnOut(TAG, "视频文件为空")
                    return
                }
                videoView.setVideoURI(uriData)
                videoView.onFocusChangeListener = onFocusChangeListener
            }
            BannerDataType.CUSTOM -> {
                onCustomDataHandleListener?.setVideoViewData(
                    context,
                    videoView,
                    bannerData
                )
                    ?: throw RuntimeException("BannerDataType为CUSTOM时，请设置OnCustomDataHandleListener接口")
                videoView.onFocusChangeListener = onFocusChangeListener
                DebugUtil.warnOut(TAG, "未处理的Banner数据类型")
            }
            else -> DebugUtil.warnOut(TAG, "未处理的Banner数据类型")
        }
    }

    /**
     * 处理图片数据
     *
     * @param imageView  图片视图
     * @param bannerData Banner数据
     */
    private fun handleImageBannerData(imageView: ImageView, bannerData: BannerData<*>) {
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (bannerData.bannerDataType) {
            BannerDataType.FILE -> {
                val fileData: File? = bannerData.fileData
                if (fileData == null) {
                    DebugUtil.warnOut(TAG, "图片文件为空")
                    return
                }
                imageView.setImageBitmap(BitmapFactory.decodeFile(fileData.absolutePath))
            }
            BannerDataType.URI -> {
                val uriData: Uri? = bannerData.uriData
                if (uriData == null) {
                    DebugUtil.warnOut(TAG, "图片文件为空")
                    return
                }
                imageView.setImageURI(uriData)
            }
            BannerDataType.CUSTOM -> {
                onCustomDataHandleListener?.setImageViewData(
                    context,
                    imageView,
                    bannerData
                )
                    ?: throw RuntimeException("BannerDataType为CUSTOM时，请设置OnCustomDataHandleListener接口")

                DebugUtil.warnOut(TAG, "未处理的Banner数据类型")
            }
            else -> DebugUtil.warnOut(TAG, "未处理的Banner数据类型")
        }
    }

    /**
     * 开启自动轮播定时器
     */
    private fun startAutoScrollTimer() {
        stopAutoScrollTimer()
        autoScrollTimer = BaseManager.newScheduledExecutorService(1)
        autoScrollTimer?.schedule({
            BaseManager.handler.post {
                viewPager?.setCurrentItem(
                    currentPosition + 1,
                    true
                )
            }
        }, delayedTime, TimeUnit.MILLISECONDS)
        DebugUtil.warnOut(TAG, "开启自动轮播定时器 delayedTime = $delayedTime")
    }

    /**
     * 停止自动轮播定时器
     */
    private fun stopAutoScrollTimer() {
        if (autoScrollTimer != null) {
            autoScrollTimer!!.shutdownNow()
        }
        autoScrollTimer = null
        DebugUtil.warnOut(TAG, "停止自动轮播定时器")
    }
}
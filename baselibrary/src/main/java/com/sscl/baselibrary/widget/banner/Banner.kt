package com.sscl.baselibrary.widget.banner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.ImageView.ScaleType
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sscl.baselibrary.files.FileProviderUtil
import com.sscl.baselibrary.utils.BaseManager
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.widget.banner.enums.BannerDataType
import com.sscl.baselibrary.widget.banner.enums.BannerType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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

        /**
         * 暂停当前显示的View的播放
         */
        fun pauseCurrent(currentPosition: Int, bannerData: BannerData<*>, view: View)
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
     * 是否压缩图片
     */
    private var needCompressPicture: Boolean = false

    /**
     * 是否压缩到指定大小
     */
    private var compressPictureWithMaxSize: Boolean = false

    /**
     * 最大的图片大小,单位为KB
     */
    private var maxCompressPictureSize: Long = 20

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
     * banner选项点击事件监听器
     */
    private var onItemClickListener: OnItemClickListener? = null

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
        adapter.setOnItemClickListener(object : BannerAdapter.OnItemClickListener {
            override fun onItemClick() {
                onItemClickListener?.onItemClick(currentPosition)
            }
        })
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

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
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
        if (viewPager?.currentItem != currentPosition) {
            viewPager?.currentItem = currentPosition
        } else {
            calculateCurrentDelayTime(viewPager?.currentItem ?: 0)
        }
    }

    /**
     * 停止自动轮播
     */
    fun stopAutoScroll() {
        enableAutoScroll = false
        stopAutoScrollTimer()
        pauseCurrent()
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
     * 设置是否压缩图片
     */
    fun setNeedCompressImage(needCompressPicture: Boolean) {
        this.needCompressPicture = needCompressPicture
    }

    /**
     * 设置是否以指定大小压缩图片
     */
    fun setCompressPictureWithMaxSize(compressPictureWithMaxSize: Boolean) {
        this.compressPictureWithMaxSize = compressPictureWithMaxSize
    }

    /**
     * 设置压缩图片的指定大小,单位为KB
     */
    fun setMaxCompressPictureSize(maxCompressPictureSize: Long) {
        this.maxCompressPictureSize = maxCompressPictureSize
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
        if (lastPosition >= 0 && adapter.views.isNotEmpty() && adapter.views.size > lastPosition && lastPosition != position) {
            //noinspection rawtypes
            val bannerData = bannerDataList[lastPosition]
            if (bannerData.bannerType == BannerType.VIDEO) {
                val view = adapter.views[lastPosition] as RelativeLayout
                val videoView = view.getChildAt(0) as VideoView
                pauseVideo(videoView)
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
                    DebugUtil.warnOut(TAG, "计算视频时间并播放")
                    playVideo(videoView)
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
                val layoutParamsRelativeLayout =
                    videoView.layoutParams as RelativeLayout.LayoutParams
                layoutParamsRelativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT)
                videoView.layoutParams = layoutParamsRelativeLayout
                adapter.views.add(relativeLayout)
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
                if (hasFocus) {
                    DebugUtil.warnOut(TAG, "视频获取到焦点，开始播放")
                    playVideo(videoView)
                } else {
                    DebugUtil.warnOut(TAG, "视频失去焦点，暂停播放")
                    pauseVideo(videoView)
                }
//                if (!hasFocus) {
//                    videoView.pause()
//                }
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

    private fun pauseVideo(videoView: VideoView) {
        if (videoView.isPlaying) {
            videoView.pause()
        } else {
            DebugUtil.warnOut(TAG, "视频没有播放，不需要暂停")
        }
    }

    private fun playVideo(videoView: VideoView) {
        if (!videoView.isPlaying) {
            videoView.start()
            videoView.seekTo(0)
        } else {
            DebugUtil.warnOut(TAG, "视频已经播放，不需要播放")
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
                loadFileToImageView(imageView, fileData)
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

    private fun loadFileToImageView(imageView: ImageView, fileData: File) {
        val filePath = fileData.absolutePath
        if (!needCompressPicture) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath))
        } else {
            val options = BitmapFactory.Options()
            //inJustDecodeBounds=true代表仅仅获取图片信息而不直接加载进入内存
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            // 获取图片的宽高
            val imgWidth: Int = options.outWidth

            val imgHeight: Int = options.outHeight
            // 获取当前手机屏幕的宽高
            val screenWidth: Int
            val screenHeight: Int
            val windowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                screenWidth = windowManager.currentWindowMetrics.bounds.width()
                screenHeight = windowManager.currentWindowMetrics.bounds.height()
            } else {
                @Suppress("DEPRECATION")
                screenWidth = windowManager.defaultDisplay.width
                @Suppress("DEPRECATION")
                screenHeight = windowManager.defaultDisplay.height
            }
            // 设置默认缩放比为1
            var scale = 1
            // 计算图片宽高与屏幕宽高比例，即计算宽缩放比，高缩放比
            val scaleWidth = imgWidth / screenWidth
            val scaleHeight = imgHeight / screenHeight
            // 选择缩放比例，如果图片比屏幕小，就不进行缩放.如果图片比屏幕大，但是宽高缩放比例不同，选择缩放比大
            if (scaleWidth >= scaleHeight && scaleWidth > 1) {
                scale = scaleWidth
            } else if (scaleWidth < scaleHeight && scaleHeight > 1) {
                scale = scaleHeight
            }
            // 在Options的对象中设置缩放比例
            options.inSampleSize = scale
            // 一定要把inJustDecodeBound该字段设置为false，实际上默认值是false，
            // 但是在前面的代码中已经改为了true，所以要更改过来。当然，也可以重新new 一个Option是对象
            options.inJustDecodeBounds = false
            val bm = BitmapFactory.decodeFile(filePath, options)
            if (!compressPictureWithMaxSize) {
                imageView.setImageBitmap(bm)
            } else {
                val compressImage = compressImage(bm)
                imageView.setImageBitmap(compressImage)
            }
        }
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    fun compressImage(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > maxCompressPictureSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset() // 重置baos即清空baos
            image.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            ) // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 1 // 每次都减少1
        }
        val isBm =
            ByteArrayInputStream(baos.toByteArray()) // 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
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
        autoScrollTimer?.shutdownNow()
        autoScrollTimer = null
        DebugUtil.warnOut(TAG, "停止自动轮播定时器")
    }

    /**
     * 暂停当前banner
     */
    private fun pauseCurrent() {
        val bannerData = bannerDataList[currentPosition]
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (bannerData.bannerType) {
            BannerType.VIDEO -> {
                val relativeLayout = adapter.views[currentPosition] as RelativeLayout
                val videoView = relativeLayout.getChildAt(0) as VideoView
                DebugUtil.warnOut(TAG, "暂停当前banner videoView")
                pauseVideo(videoView)
            }
            BannerType.CUSTOM -> {
                onCustomDataHandleListener?.pauseCurrent(
                    currentPosition,
                    bannerDataList[currentPosition],
                    adapter.views[currentPosition]
                )
            }
            BannerType.IMAGE -> {
                DebugUtil.warnOut(TAG, "当前为图片类型，不需要暂停")
            }
            else -> {
                DebugUtil.warnOut(TAG, "未处理的Banner数据类型")
            }
        }
    }
}
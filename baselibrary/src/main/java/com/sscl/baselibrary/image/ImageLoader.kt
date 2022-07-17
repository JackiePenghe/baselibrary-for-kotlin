package com.sscl.baselibrary.image

import android.content.*
import android.graphics.*
import android.widget.*
import androidx.annotation.DrawableRes
import com.sscl.baselibrary.R
import com.sscl.baselibrary.files.FileCache
import com.sscl.baselibrary.utils.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 图片加载类
 *
 * @author pengh
 */
class ImageLoader private constructor(context: Context) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 内存缓存工具
     */
    var memoryCache = MemoryCache()

    /**
     * 默认图片
     */
    var defaultDrawableRes = R.mipmap.ic_launcher

    /**
     * 图片边线宽度
     */
    var strokeWidth = 0

    /**
     * 图片是否是圆形
     */
    private var isCircle = false

    /**
     * 是否压缩图片
     */
    private var compress = false

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 文件缓存工具
     */
    private val fileCache: FileCache

    /**
     * 存储图片空间的集合
     */
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView?, String>())

    /**
     * 线程池
     */
    private val threadPoolExecutor: ThreadPoolExecutor

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 显示图片
     *
     * @param url       文件url
     * @param imageView ImageView
     * @param circle    是否显示圆形图片
     */
    fun displayImage(url: String, imageView: ImageView, circle: Boolean) {
        isCircle = circle
        imageViews[imageView] = url
        // 先从内存缓存中查找
        val bitmap = memoryCache[url]
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(defaultDrawableRes)
            // 若没有的话则开启新线程加载图片
            loadBitmapFromNet(url, imageView)
        }
    }

    /**
     * 获取图片 先从缓存中去查找，如果没有再从网络下载
     *
     * @param url 图片地址
     * @return 位图图片
     */
    fun getBitmap(url: String): Bitmap? {
        val f = fileCache.getFile(url)
        // 先从文件缓存中查找是否有
        val b = decodeFile(f)
        return b
            ?: try {
                var bitmap: Bitmap? = null
                val imageNetworkAsyncTask = ImageNetworkAsyncTask(f)
                var isLoaded = false
                imageNetworkAsyncTask.execute(url,
                    object : ImageNetworkAsyncTask.OnImageDownloadListener {
                        override fun onImageDownload(file: File) {
                            bitmap = decodeFile(file)
                            isLoaded = true
                        }
                    })
                while (true) {
                    if (isLoaded) {
                        break
                    }
                }
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
    }

    /**
     * 清除缓存
     */
    fun clearCache() {
        memoryCache.clear()
        fileCache.clear()
    }

    /**
     * 设置默认的图片
     *
     * @param defaultDrawable 默认的图片
     */
    fun setDefaultDrawable(@DrawableRes defaultDrawable: Int) {
        this.defaultDrawableRes = defaultDrawable
    }

    /**
     * 设置是否压缩图片
     *
     * @param isCompress 要设置是否压缩图片的标志
     */
    fun setCompress(isCompress: Boolean) {
        compress = isCompress
    }
    /*--------------------------------库内函数--------------------------------*/
    /**
     * 防止图片错位
     *
     * @param holder BitmapHolder
     * @return false表示错位
     */
    fun imageViewReused(holder: BitmapHolder): Boolean {
        val tag = imageViews[holder.imageView]
        return tag == null || tag != holder.url
    }

    /**
     * 加载网络图片
     *
     * @param url       网络url
     * @param imageView ImageView
     */
    private fun loadBitmapFromNet(url: String, imageView: ImageView) {
        val bitmapHolder = BitmapHolder(url, imageView)
        threadPoolExecutor.submit(BitmapLoader(bitmapHolder, this@ImageLoader))
    }

    /**
     * 压缩图片
     *
     * @param f 图片的本地路径
     * @return 位图图片
     */
    private fun decodeFile(f: File?): Bitmap? {
        if (f == null) {
            return null
        }
        val absolutePath = f.absolutePath
        DebugUtil.warnOut(TAG, "file absolutePath = $absolutePath")
        return try {
            if (!f.exists()) {
                f.createNewFile()
            }
            val bitmap: Bitmap?
            if (compress) {
                // 不加载图片的情况下获得图片的宽高
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                BitmapFactory.decodeStream(FileInputStream(f), null, o)
                val requiredSize = 70
                var widthTmp = o.outWidth
                var heightTmp = o.outHeight
                var scale = 1
                // 如果长或宽大于70，那么把图片的高宽缩小一半
                while (widthTmp / 2 >= requiredSize
                    && heightTmp / 2 >= requiredSize
                ) {
                    widthTmp /= 2
                    heightTmp /= 2
                    scale *= 2
                }
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                // 把图片的高宽缩小一半
                bitmap = BitmapFactory.decodeStream(
                    FileInputStream(f),
                    null, o2
                )
            } else {
                val o = BitmapFactory.Options()
                bitmap = BitmapFactory.decodeStream(
                    FileInputStream(f),
                    null, o
                )
            }
            if (bitmap == null) {
                return null
            }
            if (isCircle) createCircleBitmap(bitmap) else bitmap
        } catch (e: FileNotFoundException) {
            null
        } catch (e: IOException) {
            null
        }
    }

    /**
     * 把 源圆片 加工成 圆形图片
     *
     * @param resource 源圆片
     * @return 位图图片
     */
    private fun createCircleBitmap(resource: Bitmap): Bitmap {
        val width = resource.width.toFloat()
        val paint = Paint()
        // 画圆或者弧形图，需要抗锯齿
        paint.isAntiAlias = true

        // 创建一张空图片, 这张图片只有宽高，没有内容
        val target = Bitmap.createBitmap(width.toInt(), width.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(target)

        // 画一个和原图片宽高一样的内切圆
        canvas.drawCircle(
            width / 2, width / 2, (width - strokeWidth) / 2,
            paint
        )

        // 取两图的交集(也就是重合的部分)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        // 把源图覆盖上去
        canvas.drawBitmap(resource, 0f, 0f, paint)
        return target
    }

    companion object {
        private val TAG = ImageLoader::class.java.simpleName

        /**
         * 图片加载器单例
         */
        private var instance: ImageLoader? = null
        /*--------------------------------公开静态函数--------------------------------*/
        /**
         * 获取ImageLoader单例
         *
         * @param context 上下文对象
         * @return 当前ImageLoader对象
         */
        @kotlin.jvm.JvmStatic
        fun getInstance(context: Context): ImageLoader {
            if (instance == null) {
                synchronized(ImageLoader::class.java) {
                    if (instance == null) {
                        instance = ImageLoader(context)
                    }
                }
            }
            return instance!!
        }
    }
    /*--------------------------------构造函数--------------------------------*/ /**
     * 构造器
     *
     * @param context 上下文
     */
    init {
        fileCache = FileCache(context)
        threadPoolExecutor = ThreadPoolExecutor(
            2,
            10,
            0,
            TimeUnit.MINUTES,
            ArrayBlockingQueue(1024),
            BaseManager.threadFactory,
            ThreadPoolExecutor.AbortPolicy()
        )
    }
}
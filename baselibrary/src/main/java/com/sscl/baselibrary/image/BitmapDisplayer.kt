package com.sscl.baselibrary.image

import android.graphics.Bitmap
import java.lang.ref.WeakReference

/**
 * 图片显示器
 *
 * @author alm
 */
class BitmapDisplayer internal constructor(
    bitmap: Bitmap,
    bitmapHolder: BitmapHolder,
    imageLoader: ImageLoader
) : Runnable {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 位图图像
     */
    private val mBitmap: Bitmap?

    /**
     * ImageLoader弱引用
     */
    private val imageLoaderWeakReference: WeakReference<ImageLoader>

    /**
     * 位图图像Holder
     */
    private val mBitmapHolder: BitmapHolder

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 构造方法
     */
    init {
        imageLoaderWeakReference = WeakReference(imageLoader)
        mBitmap = bitmap
        mBitmapHolder = bitmapHolder
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun run() {
        val imageLoader = imageLoaderWeakReference.get() ?: return
        if (imageLoader.imageViewReused(mBitmapHolder)) {
            return
        }
        if (mBitmap != null) {
            mBitmapHolder.imageView.setImageBitmap(mBitmap)
        } else {
            mBitmapHolder.imageView.setImageResource(imageLoader.defaultDrawableRes)
        }
    }

}
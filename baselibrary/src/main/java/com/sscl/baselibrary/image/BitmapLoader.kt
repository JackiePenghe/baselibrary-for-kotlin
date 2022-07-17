package com.sscl.baselibrary.image

import java.lang.ref.WeakReference

/**
 * 位图图像加载工具
 *
 * @author alm
 */
class BitmapLoader(bitmapHolder: BitmapHolder, imageLoader: ImageLoader) : Runnable {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 私有常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 位图图像Holder弱引用
     */
    private val bitmapHolderWeakReference: WeakReference<BitmapHolder>

    /**
     * ImageLoader弱引用
     */
    private val imageLoaderWeakReference: WeakReference<ImageLoader>

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 构造方法
     */
    init {
        bitmapHolderWeakReference = WeakReference(bitmapHolder)
        imageLoaderWeakReference = WeakReference(imageLoader)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun run() {
        val bitmapHolder = bitmapHolderWeakReference.get() ?: return
        val imageLoader = imageLoaderWeakReference.get() ?: return
        // 如果没有错位那么不做任何处理，如果错位那么需要再做一次加载处理
        if (imageLoader.imageViewReused(bitmapHolder)) {
            return
        }
        val bmp = imageLoader.getBitmap(bitmapHolder.url) ?: return
        imageLoader.memoryCache.put(bitmapHolder.url, bmp)
        if (imageLoader.imageViewReused(bitmapHolder)) {
            return
        }
        val displayer = BitmapDisplayer(bmp, bitmapHolder, imageLoader)
        // 更新的操作放在UI线程中
        bitmapHolder.imageView.post(displayer)
    }
}
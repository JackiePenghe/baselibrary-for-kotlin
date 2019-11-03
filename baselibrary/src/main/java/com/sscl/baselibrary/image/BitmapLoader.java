package com.sscl.baselibrary.image;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 位图图像加载工具
 *
 * @author alm
 */

@SuppressWarnings("WeakerAccess")
public class BitmapLoader implements Runnable {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 位图图像Holder弱引用
     */
    private WeakReference<BitmapHolder> bitmapHolderWeakReference;
    /**
     * ImageLoader弱引用
     */
    private WeakReference<ImageLoader> imageLoaderWeakReference;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param bitmapHolder 位图图像Holder
     * @param imageLoader  ImageLoader
     */
    public BitmapLoader(@NonNull BitmapHolder bitmapHolder, @NonNull ImageLoader imageLoader) {
        bitmapHolderWeakReference = new WeakReference<>(bitmapHolder);
        imageLoaderWeakReference = new WeakReference<>(imageLoader);
    }

    /*--------------------------------实现接口方法--------------------------------*/

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        BitmapHolder bitmapHolder = bitmapHolderWeakReference.get();
        if (bitmapHolder == null) {
            return;
        }
        ImageLoader imageLoader = imageLoaderWeakReference.get();
        if (imageLoader == null) {
            return;
        }
        // 如果没有错位那么不做任何处理，如果错位那么需要再做一次加载处理
        if (imageLoader.imageViewReused(bitmapHolder)) {
            return;
        }
        Bitmap bmp = imageLoader.getBitmap(bitmapHolder.url);
        if (bmp == null){
            return;
        }
        imageLoader.memoryCache.put(bitmapHolder.url, bmp);

        if (imageLoader.imageViewReused(bitmapHolder)) {
            return;
        }
        BitmapDisplayer displayer = new BitmapDisplayer(bmp, bitmapHolder, imageLoader);
        // 更新的操作放在UI线程中
        bitmapHolder.imageView.post(displayer);

    }
}


package com.sscl.baselibrary.image;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 图片显示器
 *
 * @author alm
 */
public class BitmapDisplayer implements Runnable {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 位图图像
     */
    private Bitmap mBitmap;
    /**
     * ImageLoader弱引用
     */
    private WeakReference<ImageLoader> imageLoaderWeakReference;
    /**
     * 位图图像Holder
     */
    private BitmapHolder mBitmapHolder;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param bitmap       位图图像
     * @param bitmapHolder 位图图像Holder
     * @param imageLoader  ImageLoader
     */
    BitmapDisplayer(@NonNull Bitmap bitmap,@NonNull BitmapHolder bitmapHolder,@NonNull ImageLoader imageLoader) {
        imageLoaderWeakReference = new WeakReference<>(imageLoader);
        this.mBitmap = bitmap;
        mBitmapHolder = bitmapHolder;
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
        ImageLoader imageLoader = imageLoaderWeakReference.get();
        if (imageLoader == null) {
            return;
        }
        if (imageLoader.imageViewReused(mBitmapHolder)) {
            return;
        }

        if (mBitmap != null) {
            mBitmapHolder.imageView.setImageBitmap(mBitmap);
        } else {
            mBitmapHolder.imageView.setImageResource(imageLoader.defaultDrawable);
        }
    }
}
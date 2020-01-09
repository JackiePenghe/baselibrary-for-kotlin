package com.sscl.baselibrary.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.ImageView;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.files.FileCache;
import com.sscl.baselibrary.utils.BaseManager;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.MemoryCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 图片加载类
 *
 * @author pengh
 */
public class ImageLoader {

    /*--------------------------------静态变量--------------------------------*/

    private static final String TAG = ImageLoader.class.getSimpleName();

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 内存缓存工具
     */
    MemoryCache memoryCache = new MemoryCache();

    /**
     * 文件缓存工具
     */
    private FileCache fileCache;

    /**
     * 存储图片空间的集合
     */
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 默认图片
     */
    final int defaultDrawable = R.mipmap.ic_launcher;

    /**
     * 图片边线宽度
     */
    private int strokeWidth = 0;

    /**
     * 图片是否是圆形
     */
    private boolean isCircle;

    /**
     * 图片加载器单例
     */
    private static ImageLoader instance;

    /**
     * 是否压缩图片
     */
    private boolean compress;

    /*--------------------------------构造函数--------------------------------*/

    /**
     * 构造器
     *
     * @param context 上下文
     */
    private ImageLoader(@NonNull Context context) {
        fileCache = new FileCache(context);
        threadPoolExecutor = new ThreadPoolExecutor(2, 10, 0, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(1024), BaseManager.getThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    /*--------------------------------公开静态函数--------------------------------*/

    /**
     * 获取ImageLoader单例
     *
     * @param context 上下文对象
     * @return 当前ImageLoader对象
     */
    public static ImageLoader getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader(context);
                }
            }
        }
        return instance;
    }

    /*--------------------------------公开函数--------------------------------*/

    /**
     * 显示图片
     *
     * @param url       文件url
     * @param imageView ImageView
     * @param circle    是否显示圆形图片
     */
    public void displayImage(@NonNull String url, @NonNull ImageView imageView, boolean circle) {
        isCircle = circle;
        imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(defaultDrawable);
            // 若没有的话则开启新线程加载图片
            loadBitmapFromNet(url, imageView);
        }
    }

    /**
     * 获取图片 先从缓存中去查找，如果没有再从网络下载
     *
     * @param url 图片地址
     * @return 位图图片
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    public Bitmap getBitmap(@NonNull String url) {
        File f = fileCache.getFile(url);

        // 先从文件缓存中查找是否有
        Bitmap b = decodeFile(f);
        if (b != null) {
            return b;
        }

        // 最后从指定的url中下载图片
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            copyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 清除缓存
     */
    @SuppressWarnings("unused")
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    /**
     * 设置是否压缩图片
     *
     * @param isCompress 要设置是否压缩图片的标志
     */
    @SuppressWarnings("unused")
    public void setCompress(boolean isCompress) {
        compress = isCompress;
    }

    /**
     * 获取边线的宽度
     *
     * @return 边线的宽度
     */
    @SuppressWarnings("unused")
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置边线的宽度
     *
     * @param strokeWidth 边线的宽度
     */
    @SuppressWarnings("unused")
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /*--------------------------------库内函数--------------------------------*/

    /**
     * 防止图片错位
     *
     * @param holder BitmapHolder
     * @return false表示错位
     */
    boolean imageViewReused(@NonNull BitmapHolder holder) {
        String tag = imageViews.get(holder.imageView);

        return tag == null || !tag.equals(holder.url);
    }

    /*--------------------------------私有函数--------------------------------*/

    /**
     * 下载图片操作
     *
     * @param is InputStream
     * @param os OutputStream
     */
    private void copyStream(@NonNull InputStream is, @NonNull OutputStream os) {
        final int bufferSize = 1024;
        try {
            byte[] bytes = new byte[bufferSize];
            while (true) {
                int count = is.read(bytes, 0, bufferSize);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            DebugUtil.warnOut(TAG, "copyStream with exception! " + e.getMessage());
        }
    }

    /**
     * 加载网络图片
     *
     * @param url       网络url
     * @param imageView ImageView
     */
    private void loadBitmapFromNet(String url, ImageView imageView) {
        BitmapHolder bitmapHolder = new BitmapHolder(url, imageView);
        threadPoolExecutor.submit(new BitmapLoader(bitmapHolder, ImageLoader.this));
    }

    /**
     * 压缩图片
     *
     * @param f 图片的本地路径
     * @return 位图图片
     */
    private Bitmap decodeFile(File f) {
        try {
            Bitmap bitmap;
            if (compress) {
                // 不加载图片的情况下获得图片的宽高
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                final int requiredSize = 70;
                int widthTmp = o.outWidth, heightTmp = o.outHeight;
                int scale = 1;
                // 如果长或宽大于70，那么把图片的高宽缩小一半
                while (widthTmp / 2 >= requiredSize
                        && heightTmp / 2 >= requiredSize) {
                    widthTmp /= 2;
                    heightTmp /= 2;
                    scale *= 2;
                }

                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                // 把图片的高宽缩小一半
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
                        null, o2);
            } else {
                BitmapFactory.Options o = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
                        null, o);
            }
            if (bitmap == null) {
                return null;
            }
            return isCircle ? createCircleBitmap(bitmap) : bitmap;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 把 源圆片 加工成 圆形图片
     *
     * @param resource 源圆片
     * @return 位图图片
     */
    private Bitmap createCircleBitmap(Bitmap resource) {
        float width = resource.getWidth();
        Paint paint = new Paint();
        // 画圆或者弧形图，需要抗锯齿
        paint.setAntiAlias(true);

        // 创建一张空图片, 这张图片只有宽高，没有内容
        Bitmap target = Bitmap.createBitmap((int) width, (int) width, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(target);

        // 画一个和原图片宽高一样的内切圆
        canvas.drawCircle(width / 2, width / 2, (width - strokeWidth) / 2,
                paint);

        // 取两图的交集(也就是重合的部分)
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 把源图覆盖上去
        canvas.drawBitmap(resource, 0, 0, paint);

        return target;
    }
}

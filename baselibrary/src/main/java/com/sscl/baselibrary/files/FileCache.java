package com.sscl.baselibrary.files;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.utils.DebugUtil;

import java.io.File;

/**
 * 文件缓存工具
 *
 * @author ALM
 */
public class FileCache {

    /*--------------------------------静态常量--------------------------------*/

    private static final String TAG = FileCache.class.getSimpleName();

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 缓存目录
     */
    private final File cacheDir;

    /*--------------------------------构造函数--------------------------------*/

    /**
     * 构造器
     * @param context 上下文
     */
    public FileCache(Context context) {
        // 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片
        // 没有SD卡就放在系统的缓存目录中
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = FileUtil.getCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }

        if (cacheDir == null) {
            throw new NullPointerException("cacheDir == null");
        }

        //如果目录不存在，那么创建一个缓存目录
        if (!cacheDir.exists()) {
            boolean mkdirs = cacheDir.mkdirs();
            DebugUtil.warnOut(TAG,"mkdirs " + mkdirs);
        }
    }

    /*--------------------------------公开函数--------------------------------*/

    /**
     * 根据url获取缓存文件
     *
     * @param url 缓存文件url
     * @return 缓存文件
     */
    @NonNull
    public File getFile(@NonNull String url) {
        String[] split = url.split("\\.");
        String fileName;
        int length = split.length;
        if (length == 2) {
            fileName = split[0];
        } else {
            if (length > 2) {
                length = length - 1;
                StringBuilder cache = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    cache.append(split[i]);
                }
                fileName = cache.toString();
            } else {
                fileName = url;
            }
        }
        split = fileName.split("/");
        length = split.length;
        if (split.length != 1) {
            fileName = split[length - 1];
        }

        return new File(cacheDir, fileName);

    }

    /*--------------------------------库内函数--------------------------------*/

    /**
     * 清除缓存
     */
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            boolean delete = f.delete();
            DebugUtil.warnOut(TAG,"delete file " + f.getName() + delete);
        }
    }

}

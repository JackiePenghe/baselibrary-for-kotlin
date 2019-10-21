package com.sscl.baselibrary.files;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 *
 * @author ALM
 */
public class FileUtil {

    /*--------------------------------静态常量--------------------------------*/

    /**
     * 在文件管理器中的程序文件夹名（默认值，未执行初始化函数就是这个值）
     */
    private static final String UNNAMED_APP_DIR = "UnnamedAppDir";

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 初始化状态
     */
    private static boolean init = false;

    /**
     * 应用程序名(在文件管理器中的程序文件夹名)
     */
    private static String APP_NAME = UNNAMED_APP_DIR;

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public static void init(@NonNull Context context) {
        init(context, true);
    }

    /**
     * 初始化
     *
     * @param context            上下文
     * @param withOutPackageName 是否需要包名
     */
    @SuppressWarnings("WeakerAccess")
    public static void init(@NonNull Context context, boolean withOutPackageName) {
        File filesDir = context.getFilesDir();
        String absolutePath = filesDir.getAbsolutePath();
        String[] split = absolutePath.split("/");
        String appName = split[split.length - 2];
        if (withOutPackageName) {
            String[] split1 = appName.split("\\.");
            appName = split1[split1.length - 1];
        }
        FileUtil.APP_NAME = appName;
        init = true;
    }

    /**
     * 获取SD卡文件目录
     *
     * @return 获取SD卡路径
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static File getSdCardDir() {
        //获取挂载状态
        String state = Environment.getExternalStorageState();

        //如果是已挂载,说明了有内存卡
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return Environment.getExternalStorageDirectory();
        }
        throw new NullPointerException("No SD card was found!");
    }

    /**
     * 获取本项目文件目录
     *
     * @return 本项目文件目录
     */
    @Nullable
    public static File getAppDir() {

        if (!init) {
            throw new NullPointerException("FileUtil not init");
        }
        if (null == APP_NAME || "".equals(APP_NAME)) {
            throw new ExceptionInInitializerError("File name invalid");
        }
        File file = new File(getSdCardDir(), APP_NAME);
        boolean mkdirs;
        if (!file.exists()) {
            mkdirs = file.mkdirs();
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 获取项目缓存文件目录
     *
     * @return 项目缓存文件目录
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static File getCacheDir() {
        File file = new File(getAppDir(), "cache");
        boolean mkdirs;
        if (!file.exists()) {
            mkdirs = file.mkdirs();
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 获取项目的异常日志目录
     *
     * @return 项目的异常日志目录
     */
    @Nullable
    public static File getCrashDir() {
        File file = new File(getAppDir(), "crash");
        boolean mkdirs;
        if (!file.exists()) {
            mkdirs = file.mkdirs();
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 获取apk存储目录
     *
     * @return apk存储目录
     */
    @SuppressWarnings("unused")
    @Nullable
    public static File getApkDir() {
        File file = new File(getAppDir(), "apk");
        boolean mkdirs;
        if (!file.exists()) {
            mkdirs = file.mkdirs();
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 获取图片目录
     *
     * @return 图片目录
     */
    @SuppressWarnings("unused")
    @Nullable
    public static File getImageDir() {
        File file = new File(getAppDir(), "images");
        boolean mkdirs;
        if (!file.exists()) {
            mkdirs = file.mkdirs();
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param apkFile 文件
     */
    @SuppressWarnings("unused")
    public static void installApk(@NonNull Context context,@NonNull File apkFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(apkFile), type);
        context.startActivity(intent);
    }

    /**
     * 获取项目数据目录
     *
     * @return 项目数据目录
     */
    @SuppressWarnings("unused")
    @Nullable
    public static File getDataDir() {
        File file = new File(getAppDir(), "data");
        boolean mkdirs;
        if (!file.exists()) {
            mkdirs = file.mkdirs();
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    @SuppressWarnings("unused")
    public static boolean delFile(@NonNull String fileName) {
        File file = new File(getAppDir(), fileName);
        if (file.isFile()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            return true;
        }
        //noinspection ResultOfMethodCallIgnored
        file.exists();
        return false;
    }

    /**
     * 保存图片
     *
     * @param bm      位图图片
     * @param picName 文件名
     */
    @SuppressWarnings("unused")
    public static void saveBitmap(@NonNull Bitmap bm,@NonNull String picName) {
        Log.e("", "保存图片");
        try {

            File f = new File(getCacheDir(), picName + ".JPEG");
            if (f.exists()) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }

            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return true表示文件存在
     */
    @SuppressWarnings("unused")
    public static boolean isFileExist(@NonNull String fileName) {
        File file = new File(getAppDir(), fileName);
        //noinspection ResultOfMethodCallIgnored
        file.isFile();
        return file.exists();
    }

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return true表示文件存在
     */
    @SuppressWarnings("unused")
    public static boolean fileIsExists(@NonNull String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }
}

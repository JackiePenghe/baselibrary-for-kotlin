package com.sscl.baselibrary.files;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.validation.Schema;


/**
 * FileProvider工具类
 *
 * @author ALM
 */
public class FileProviderUtil extends FileProvider {

    /*--------------------------------静态常量--------------------------------*/

    private static final String CONTENT_CONTENT = "content";
    private static final String ROOT_PATH = "/root";

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 根据File获取文件Uri
     *
     * @param context 上下文
     * @param file    File对象
     * @return 文件Uri
     */
    @NonNull
    public static Uri getUriFromFile(@NonNull Context context, @NonNull File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriFromFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * 设置intent数据和type
     *
     * @param context  上下文
     * @param intent   Intent
     * @param type     type
     * @param file     文件
     * @param canWrite 是否可写
     */
    @SuppressWarnings("unused")
    public static void setIntentDataAndType(@NonNull Context context, @NonNull Intent intent, @NonNull String type, @NonNull File file, boolean canWrite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getUriFromFile24(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (canWrite) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    /**
     * 设置intent数据
     *
     * @param context  上下文
     * @param intent   intent
     * @param file     文件
     * @param canWrite 是否可写
     */
    @SuppressWarnings("unused")
    public static void setIntentData(@NonNull Context context, @NonNull Intent intent, @NonNull File file, boolean canWrite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setData(getUriFromFile24(context, file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (canWrite) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setData(Uri.fromFile(file));
        }
    }

    /**
     * 加入权限
     *
     * @param context  上下文
     * @param intent   intent
     * @param uri      文件Uri
     * @param canWrite 是否可写
     */
    @SuppressWarnings("unused")
    public static void grantPermissions(@NonNull Context context, @NonNull Intent intent, @NonNull Uri uri, boolean canWrite) {

        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (canWrite) {
            flag |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        }
        intent.addFlags(flag);
        List<ResolveInfo> resInfoList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, flag);
        }
    }

    /**
     * 根据文件Uri获取文件路径
     *
     * @param uri 文件Uri
     * @return 文件路径
     */
    @NonNull
    static String getPath(@NonNull Context context,@NonNull Uri uri) {
        String scheme = uri.getScheme();
        if (!CONTENT_CONTENT.equals(scheme)) {
            throw new RuntimeException("Uri scheme error! Need " + CONTENT_CONTENT + ",find " + scheme + ".");
        }
        if (!uri.isAbsolute()) {
            throw new RuntimeException("Uri must be absolute");
        }
        String path = uri.getPath();
        if (path == null) {
            return "";
        }
        if (path.startsWith(ROOT_PATH)) {
            path = path.substring(ROOT_PATH.length());
        }
        return path;
    }

    /*--------------------------------私有静态方法--------------------------------*/

    /**
     * 根据File获取文件Uri（需要至少API24）
     *
     * @param context 上下文
     * @param file    File对象
     * @return 文件Uri
     */
    @NonNull
    @TargetApi(Build.VERSION_CODES.N)
    private static Uri getUriFromFile24(@NonNull Context context,@NonNull File file) {
        String authority = context.getPackageName() + ".fileprovider";
        return FileProvider.getUriForFile(context, authority, file);
    }
}

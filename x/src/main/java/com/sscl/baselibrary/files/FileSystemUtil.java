package com.sscl.baselibrary.files;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * 文件管理相关工具类
 *
 * @author ALM
 */
@SuppressWarnings("unused")
public class FileSystemUtil {

    /*--------------------------------私有静态常量--------------------------------*/

    private static final String PRIMARY = "primary";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String AUDIO = "audio";
    private static final String CONTENT = "content";
    private static final String FILE = "file";


    /*--------------------------------公开静态常量--------------------------------*/

    @SuppressWarnings("WeakerAccess")
    public static final String FILE_TYPE_ALL = "*/*";

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 根据uri获取文件路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件路径
     */
    @Nullable
    @SuppressWarnings("unused")
    public static String getPath(@NonNull final Context context, @NonNull final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if (PRIMARY.equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if (IMAGE.equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (VIDEO.equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if (AUDIO.equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (CONTENT.equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if (FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * 打开文件管理(需要根据请求码在onActivityResult中获取数据
     * if(requestCode == your requestCode && resultCode == Activity.RESULT_OK)){
     * Uri uri = intent.getData;//获取文件uri
     * }
     *
     * @param activity    对应的Activity
     * @param requestCode 请求码
     * @return true表示打开成功
     */
    @SuppressWarnings("unused")
    public static boolean openSystemFile(@NonNull Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(FILE_TYPE_ALL);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据文件类型打开某一个文件
     *
     * @param context  上下文
     * @param file     文件
     * @param fileType 文件类型
     */
    @SuppressWarnings("WeakerAccess")
    public static void openFile(@NonNull Context context,@NonNull File file,@NonNull String fileType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uriFromFile = FileProviderUtil.getUriFromFile(context, file.getAbsoluteFile());
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uriFromFile, fileType);
        context.startActivity(intent);
    }

    /**
     * 打开某一个文件
     *
     * @param context 上下文
     * @param file    文件
     */
    @SuppressWarnings("unused")
    public static void openFile(@NonNull Context context,@NonNull File file) {
        openFile(context, file, FILE_TYPE_ALL);
    }

    /**
     * 读取文件内容
     *
     * @param context    上下文
     * @param filePath   文件路径
     * @param byteLength 要读取的文件内容长度(为0，则表示全部读完)
     * @return 读取的文件内容
     */
    @Nullable
    @SuppressWarnings("unused")
    public static byte[] readFileFromPath(@NonNull Context context,@NonNull String filePath, int byteLength) {
        //若果SD卡存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fileInputStream = null;
            File file = new File(filePath);
            try {
                fileInputStream = new FileInputStream(file);
                byte[] buffer;
                if (byteLength == 0) {
                    int fileSize = fileInputStream.available();
                    buffer = new byte[fileSize];
                } else {
                    if (byteLength >= fileInputStream.available()) {
                        return null;
                    }
                    buffer = new byte[byteLength];
                }
                //noinspection ResultOfMethodCallIgnored
                fileInputStream.read(buffer);

                return buffer;
            } catch (FileNotFoundException e) {
                ToastUtil.toastLong(context, R.string.file_not_found_or_use_unrecognized_characters);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        return null;
    }

    /**
     * 从当前应用程序的目录中获取某个文件的大小
     *
     * @param filePath 文件绝对路径
     * @return 文件大小(Byte)
     */
    @SuppressWarnings("unused")
    public static int getFileSizeFromRealPath(@NonNull String filePath) {
        //若果SD卡存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(filePath);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);

                return fileInputStream.available();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 0;
        }
        return 0;
    }

    /*--------------------------------私有静态方法--------------------------------*/

    /**
     * 得到这个Uri的数据列的值
     *
     * @param context       上下文
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return 数据列的值, 通常是一个文件路径
     */
    @Nullable
    private static String getDataColumn(@NonNull Context context, @Nullable Uri uri, @Nullable String selection,@Nullable String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        if (uri == null){
            return null;
        }
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    /**
     * 检测是否属于外部存储的文件
     *
     * @param uri 要检测的Uri
     * @return uri是否指向外部存储
     */
    private static boolean isExternalStorageDocument(@NonNull Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 检测是否属于下载uri
     *
     * @param uri 要检测的Uri
     * @return 是否属于下载uri
     */
    private static boolean isDownloadsDocument(@NonNull Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 检测Uri
     *
     * @param uri Uri
     * @return 是否是多媒体uri
     */
    private static boolean isMediaDocument(@NonNull Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}

package com.sscl.baselibrary.files;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.DebugUtil;
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
    private static final String TAG = FileSystemUtil.class.getSimpleName();

    /**
     * 打开文件管理(需要根据请求码在onActivityResult中获取数据
     * if(requestCode == your requestCode && resultCode == Activity.RESULT_OK)){
     * Uri uri = intent.getData;//获取文件uri
     * }
     *
     * @param activity    对应的Activity
     * @param requestCode 请求码
     * @param fileType    文件类型
     * @return true表示打开成功
     */
    @SuppressWarnings({"unused"})
    public static boolean openSystemFile(@NonNull Activity activity, int requestCode, @NonNull FileType fileType) {
        return openSystemFile(activity, requestCode, fileType.getValue());
    }

    /*--------------------------------私有静态变量--------------------------------*/

    /**
     * 文件选择路径
     */
    private static OnFileSelectedListener onFileSelectedListener;

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
        DebugUtil.warnOut(TAG,"getPath uri " + uri);
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                DebugUtil.warnOut(TAG, "file docId " + docId);
                DebugUtil.warnOut(TAG, "file type " + type);
                if (PRIMARY.equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    if (type.split("-").length == 2) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
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

        return FileProviderUtil.getPath(context,uri);
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
        return openSystemFile(activity, requestCode, FileType.FILE_TYPE_ALL);
    }

    /**
     * 打开文件管理(需要根据请求码在onActivityResult中获取数据
     * if(requestCode == your requestCode && resultCode == Activity.RESULT_OK)){
     * Uri uri = intent.getData;//获取文件uri
     * }
     *
     * @param activity    对应的Activity
     * @param requestCode 请求码
     * @param fileType    文件类型
     * @return true表示打开成功
     */
    @SuppressWarnings({"unused"})
    public static boolean openSystemFile(@NonNull Activity activity, int requestCode, @NonNull String fileType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(fileType);
        try {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        Uri uri = FileSystemUtil.getSelectFileUri(data);
        if (uri == null) {
            if (onFileSelectedListener != null) {
                onFileSelectedListener.fileSelected(requestCode, null, null);
            }
            return;
        }
        String filePath = getSelectFilePath(context, uri);
        if (onFileSelectedListener != null) {
            onFileSelectedListener.fileSelected(requestCode, uri, filePath);
        }
    }

    public enum FileType {
        /**
         * .3gp格式的文件
         */
        VIDEO_3GPP("video/3gpp"),

        /**
         * .apk格式的文件
         */
        APK_FILE("application/vnd.android.package-archive"),

        /**
         * .asf格式的文件
         */
        ASF_FILE("video/x-ms-asf"),

        /**
         * .avi格式的文件
         */
        AVI_FILE("video/x-msvideo"),

        /**
         * .bin .class .exe .gif gtar 格式的文件
         */
        OCTET_STREAM_FILE("application/octet-stream"),

        /**
         * 图片格式的文件
         */
        IMAGE_FILE("image/*"),

        /**
         * .bmp 格式的文件
         */
        BMP_FILE("image/bmp"),

        /**
         * .jpeg .jpg 格式的文件
         */
        JPEG_FILE("image/jpeg"),
        /**
         * .png格式的文件
         */
        PNG_FILE("image/png"),

        /**
         * .txt .c .conf .cpp .h .log .prop .rc .sh .xml 格式的文件
         */
        TEXT_FILE("text/plain"),

        /**
         * .doc格式的文件
         */
        DOC_FILE("application/msword"),

        /**
         * .docx格式的文件
         */
        DOCX_FILE("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

        /**
         * .xls格式的文件
         */
        XLS_FILE("application/vnd.ms-excel"),

        /**
         * .xlsx格式的文件
         */
        XLSX_FILE("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

        /**
         * .gz格式的文件
         */
        GZ_FILE("application/x-gzip"),

        /**
         * .htm .html 格式的文件
         */
        HTM_FILE("text/html"),

        /**
         * .jar .java 格式的文件
         */
        JAVA_FILE("application/java-archive"),

        /**
         * .js格式的文件
         */
        JS_FILE("application/x-javascript"),

        /**
         * m3u格式的文件
         */
        M3U_FILE("audio/x-mpegurl"),

        /**
         * .m4a .m4b .m4p 格式的文件
         */
        M4A_FILE("audio/mp4a-latm"),

        /**
         * ,m4u格式的文件
         */
        M4U_FILE("video/vnd.mpegurl"),

        /**
         * .m4v格式的文件
         */
        M4V_FILE("video/x-m4v"),

        /**
         * .mov格式的文件
         */
        MOV_FILE("video/quicktime"),

        /**
         * .mp2 .mp3格式的文件
         */
        MP3_FILE("audio/x-mpeg"),

        /**
         * .mp4 .mpg4 格式的文件
         */
        MP4_FILE("video/mp4"),

        /**
         * .mpc格式的文件
         */
        MPC_FILE("application/vnd.mpohun.certificate"),

        /**
         * .mpe .mpeg .mpg格式的文件
         */
        MPG_FILE("video/mpeg"),

        /**
         * .mpga格式的文件
         */
        MPGA_FILE("audio/mpeg"),

        /**
         * .msg格式的文件
         */
        MSG_FILE("application/vnd.ms-outlook"),
        /**
         * .ogg格式的文件
         */
        OGG_FILE("audio/ogg"),
        /**
         * .pdf格式的文件
         */
        PDF_FILE("application/pdf"),
        /**
         * .ppt .pps格式的文件
         */
        PPT_FILE("application/vnd.ms-powerpoint"),
        /**
         * .pptx格式的文件
         */
        PPTX_FILE("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        /**
         * .rmvb格式的文件
         */
        RMVB_FILE("audio/x-pn-realaudio"),
        /**
         * .rtf格式的文件
         */
        RTF_FILE("audio/x-pn-realaudio"),
        /**
         * .tar格式的文件
         */
        TAR_FILE("application/x-tar"),
        /**
         * .tgz格式的文件
         */
        TGZ_FILE("application/x-compressed"),
        /**
         * .wav格式的文件
         */
        WAV_FILE("audio/x-wav"),
        /**
         * .wma格式的文件
         */
        WMA_FILE("audio/x-ms-wma"),
        /**
         * wmv格式的文件
         */
        WMV_FILE("audio/x-ms-wmv"),
        /**
         * .wps格式的文件
         */
        WPS_FILE("application/vnd.ms-works"),
        /**
         * .z格式的文件
         */
        Z_FILE("application/x-compress"),
        /**
         * .x_zip格式的文件
         */
        X_ZIP_FILE("application/x-zip-compressed"),
        /**
         * .zip格式的文件
         */
        ZIP_FILE("application/zip"),
        /**
         * 所有APP 支持的文件
         */
        APPLICATION_ALL("application/*"),
        /**
         * 所有文件
         */
        FILE_TYPE_ALL("*/*");


        String value;

        FileType(String value) {
            this.value = value;
        }

        private String getValue() {
            return value;
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
    public static void openFile(@NonNull Context context, @NonNull File file, @NonNull FileType fileType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uriFromFile = FileProviderUtil.getUriFromFile(context, file.getAbsoluteFile());
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uriFromFile, fileType.getValue());
        context.startActivity(intent);
    }

    /**
     * 打开某一个文件
     *
     * @param context 上下文
     * @param file    文件
     */
    @SuppressWarnings("unused")
    public static void openFile(@NonNull Context context, @NonNull File file) {
        openFile(context, file, FileType.FILE_TYPE_ALL);
    }

    /**
     * 获取选择的文件所在的文件路径
     *
     * @param context 上下文
     * @param uri     Intent
     * @return 文件路径
     */
    @Nullable
    public static String getSelectFilePath(Context context, @NonNull Uri uri) {
        String path;
        //使用第三方应用打开
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        } else {
            path = FileSystemUtil.getPath(context, uri);
        }

        return path;
    }

    /**
     * 获取选择的文件的uri
     *
     * @param intent Intent
     * @return Uri
     */
    @Nullable
    public static Uri getSelectFileUri(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getData();
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
    public static byte[] readFileFromPath(@NonNull Context context, @NonNull String filePath, int byteLength) {
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

    public static void setOnFileSelectedListener(OnFileSelectedListener onFileSelectedListener) {
        FileSystemUtil.onFileSelectedListener = onFileSelectedListener;
    }

    /*--------------------------------接口定义--------------------------------*/

    public interface OnFileSelectedListener {

        /**
         * 选择的文件的路径
         *
         * @param requestCode 请求码
         * @param uri         文件URI
         * @param filePath    文件路径
         */
        void fileSelected(int requestCode, @Nullable Uri uri, @Nullable String filePath);
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
    private static String getDataColumn(@NonNull Context context, @Nullable Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        if (uri == null) {
            return null;
        }

        String result;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                result = cursor.getString(columnIndex);
            } else {
                result = FileProviderUtil.getPath(context,uri);
            }
        } catch (Exception e) {
            result = FileProviderUtil.getPath(context,uri);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
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
     * @return 是否属于下载uri解压完成
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

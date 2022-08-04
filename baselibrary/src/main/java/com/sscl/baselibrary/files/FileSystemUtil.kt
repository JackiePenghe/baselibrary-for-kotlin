package com.sscl.baselibrary.files

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.sscl.baselibrary.R
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.ToastUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 文件管理相关工具类
 *
 * @author ALM
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object FileSystemUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 枚举声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    enum class FileType constructor(val value: String) {


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
         * 所有zip文件
         */
        ALL_ZIP_FILE("*/zip"),

        /**
         * 所有APP 支持的文件
         */
        APPLICATION_ALL("application/*"),

        /**
         * 所有文件
         */
        FILE_TYPE_ALL("*/*");

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnFragmentActivityFileSelectedListener {

        fun fileSelected(resultCode: Int, uri: Uri?, filePath: String?)
    }

    interface OnActivityFileSelectedListener {
        fun fileSelected(
            requestCode: Int,
            resultCode: Int,
            uri: Uri?,
            filePath: String?
        )
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 私有常量 * * * * * * * * * * * * * * * * * * */
    private const val PRIMARY = "primary"
    private const val IMAGE = "image"
    private const val VIDEO = "video"
    private const val AUDIO = "audio"
    private const val CONTENT = "content"
    private const val FILE = "file"
    private val TAG = FileSystemUtil::class.java.simpleName

    /* * * * * * * * * * * * * * * * * * * 私有可空变量 * * * * * * * * * * * * * * * * * * */

    /**
     * 文件选择路径
     */
    private var onActivityFileSelectedListener: OnActivityFileSelectedListener? = null

    private var onFragmentActivityFileSelectedListener: OnFragmentActivityFileSelectedListener? =
        null

    /**
     * FragmentActivity 的 startActivityForResult 替代方案
     */
    private var intentActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 根据uri获取文件路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件路径
     */
    @SuppressLint("SdCardPath")
    fun getPath(context: Context, uri: Uri): String? {
        DebugUtil.warnOut(TAG, "getPath uri $uri")
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                DebugUtil.warnOut(TAG, "file docId $docId")
                DebugUtil.warnOut(TAG, "file type $type")
                if (PRIMARY.equals(type, ignoreCase = true)) {
                    return (context.getExternalFilesDir("")?.absolutePath
                        ?: "/sdcard") + "/" + split[1]
                } else {
                    if (type.split("-".toRegex()).toTypedArray().size == 2) {
                        return (context.getExternalFilesDir("")?.absolutePath
                            ?: "/sdcard") + "/" + split[1]
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                try {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong()
                    )
                    return getDataColumn(context, contentUri, null, null)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if (IMAGE == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if (VIDEO == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if (AUDIO == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if (CONTENT.equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if (FILE.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return FileProviderUtil.getPath(uri)
    }

    /**
     * 根据文件类型打开某一个文件
     *
     * @param context  上下文
     * @param file     文件
     * @param fileType 文件类型
     */
    fun openFile(context: Context, file: File, fileType: FileType = FileType.FILE_TYPE_ALL) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uriFromFile = FileProviderUtil.getUriFromFile(context, file.absoluteFile)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uriFromFile, fileType.value)
        context.startActivity(intent)
    }

    /**
     * 获取选择的文件所在的文件路径
     *
     * @param context 上下文
     * @param uri     Intent
     * @return 文件路径
     */
    fun getSelectFilePath(context: Context, uri: Uri): String? {
        //使用第三方应用打开
        val path: String? = if ("file".equals(uri.scheme, ignoreCase = true)) {
            uri.path
        } else {
            getPath(context, uri)
        }
        return path
    }

    /**
     * 获取选择的文件的uri
     *
     * @param intent Intent
     * @return Uri
     */
    fun getSelectFileUri(intent: Intent?): Uri? {
        return intent?.data
    }

    /**
     * 读取文件内容
     *
     * @param context    上下文
     * @param filePath   文件路径
     * @param byteLength 要读取的文件内容长度(为0，则表示全部读完)
     * @return 读取的文件内容
     */
    fun readFileFromPath(context: Context, filePath: String, byteLength: Int): ByteArray? {
        //若果SD卡存在
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var fileInputStream: FileInputStream? = null
            val file = File(filePath)
            try {
                fileInputStream = FileInputStream(file)
                val buffer: ByteArray = if (byteLength == 0) {
                    val fileSize = fileInputStream.available()
                    ByteArray(fileSize)
                } else {
                    if (byteLength >= fileInputStream.available()) {
                        return null
                    }
                    ByteArray(byteLength)
                }
                fileInputStream.read(buffer)
                return buffer
            } catch (e: FileNotFoundException) {
                ToastUtil.toastLong(
                    context,
                    R.string.com_jackiepenghe_file_not_found_or_use_unrecognized_characters
                )
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }
        return null
    }

    /**
     * 从当前应用程序的目录中获取某个文件的大小
     *
     * @param filePath 文件绝对路径
     * @return 文件大小(Byte)
     */
    fun getFileSizeFromRealPath(filePath: String): Int {
        //若果SD卡存在
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val file = File(filePath)
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = FileInputStream(file)
                return fileInputStream.available()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return 0
        }
        return 0
    }

    /* * * * * * * * * * * * * * * * * * * 扩展方法 * * * * * * * * * * * * * * * * * * */

    /**
     * 打开文件管理(需要根据请求码在onActivityResult中获取数据
     * if(requestCode == your requestCode && resultCode == Activity.RESULT_OK)){
     * Uri uri = intent.getData;//获取文件uri
     * }
     *
     * @param requestCode 请求码
     * @param fileType    文件类型
     * @return true表示打开成功
     */
    @JvmName("activityOpenSystemFile")
    fun Activity.openSystemFile(
        requestCode: Int,
        fileType: FileType = FileType.FILE_TYPE_ALL
    ): Boolean {
        return openSystemFile(requestCode, fileType.value)
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
    @Deprecated("FragmentActivity不支持", ReplaceWith("FragmentActivity.openSystemFile(FileType)"))
    fun FragmentActivity.openSystemFile(
        activity: Activity,
        requestCode: Int,
        fileType: FileType = FileType.FILE_TYPE_ALL
    ): Boolean {
        throw IllegalArgumentException("FragmentActivity已弃用此方法")
    }

    /**
     * 打开文件管理(需要根据请求码在onActivityResult中获取数据
     * if(requestCode == your requestCode && resultCode == Activity.RESULT_OK)){
     * Uri uri = intent.getData;//获取文件uri
     * }
     *
     * @param fileType    文件类型
     * @return true表示打开成功
     */
    @JvmName("fragmentActivityOpenSystemFile")
    fun FragmentActivity.openSystemFile(
        fileType: FileType = FileType.FILE_TYPE_ALL
    ): Boolean {
        return openSystemFile(fileType.value)
    }

    /**
     * 打开文件管理(需要根据请求码在onActivityResult中获取数据
     * if(requestCode == your requestCode && resultCode == Activity.RESULT_OK)){
     * Uri uri = intent.getData;//获取文件uri
     * }
     *
     * @param requestCode 请求码
     * @param fileType    文件类型
     * @return true表示打开成功
     */
    @JvmName("activityOpenSystemFile")
    fun Activity.openSystemFile(requestCode: Int, fileType: String): Boolean {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = fileType
        return try {
            this.startActivityForResult(intent, requestCode)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 打开文件管理
     *
     * @param fileType    文件类型
     * @return true表示打开成功
     */
    @JvmName("fragmentActivityOpenSystemFile")
    fun FragmentActivity.openSystemFile(
        fileType: String
    ): Boolean {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = fileType
        return try {
            intentActivityResultLauncher?.launch(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    /**
     * 需要将此方法放在onActivityResult中调用
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @JvmName("onActivityResult")
    fun Activity.onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val uri = getSelectFileUri(data)
        if (uri == null) {
            onActivityFileSelectedListener?.fileSelected(requestCode, resultCode, null, null)
            return
        }
        val filePath = getSelectFilePath(this, uri)
        onActivityFileSelectedListener?.fileSelected(requestCode, resultCode, uri, filePath)
    }

    /**
     * 需要将此方法放在onActivityResult中调用
     *
     * @param context     上下文
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Deprecated("FragmentActivity不支持", ReplaceWith("onStartActivity"))
    fun FragmentActivity.onActivityResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        throw UnsupportedOperationException("onActivityResult 在 FragmentActivity中被废弃了，请使用 onStartActivity 替代")
    }

    /**
     * 在FragmentActivity的 super.onStart 之前调用,注册监听器
     */
    @JvmName("onStartFragmentActivity")
    fun FragmentActivity.onStartActivity() {
        //startActivityForResult被弃用，改用ActivityResultLauncher
        intentActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                DebugUtil.warnOut(TAG, "manage result code " + result.resultCode)
                val data = result.data
                val uri = getSelectFileUri(data)
                if (uri == null) {
                    onFragmentActivityFileSelectedListener?.fileSelected(
                        result.resultCode,
                        null,
                        null
                    )
                    return@registerForActivityResult
                }
                val filePath = getSelectFilePath(this, uri)
                onFragmentActivityFileSelectedListener?.fileSelected(
                    result.resultCode,
                    uri,
                    filePath
                )
            }
    }

    /**
     * 在FragmentActivity的 super.onStart 之前调用,注册监听器
     */
    @Deprecated("Activity 不支持", ReplaceWith("Activity.onActivityResult"))
    fun Activity.onStartActivity() {
        throw UnsupportedOperationException("Activity 不支持")
    }

    @JvmName("setActivityOnFileSelectedListener")
    fun Activity.setOnFileSelectedListener(onFileSelectedListener: OnActivityFileSelectedListener?) {
        onActivityFileSelectedListener = onFileSelectedListener
    }

    @Deprecated(
        "FragmentActivity 不支持",
        ReplaceWith("FragmentActivity.setOnFileSelectedListener(onFileSelectedListener: OnFragmentActivityFileSelectedListener?)")
    )
    fun FragmentActivity.setOnFileSelectedListener(onFileSelectedListener: OnActivityFileSelectedListener?) {
        throw UnsupportedOperationException("FragmentActivity 不支持")
    }
    @JvmName("setFragmentActivityOnFileSelectedListener")
    fun FragmentActivity.setOnFileSelectedListener(onFileSelectedListener: OnFragmentActivityFileSelectedListener?) {
        onFragmentActivityFileSelectedListener = onFileSelectedListener
    }

    @Deprecated(
        "Activity 不支持",
        ReplaceWith("Activity.setOnFileSelectedListener(onFileSelectedListener: OnActivityFileSelectedListener?)")
    )
    fun Activity.setOnFileSelectedListener(onFileSelectedListener: OnFragmentActivityFileSelectedListener?) {
        throw UnsupportedOperationException("Activity 不支持")
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 得到这个Uri的数据列的值
     *
     * @param context       上下文
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return 数据列的值, 通常是一个文件路径
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        if (uri == null) {
            return null
        }
        var result: String?
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            result = if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                cursor.getString(columnIndex)
            } else {
                FileProviderUtil.getPath(uri)
            }
        } catch (e: Exception) {
            result = FileProviderUtil.getPath(uri)
        } finally {
            cursor?.close()
        }
        return result
    }

    /**
     * 检测是否属于外部存储的文件
     *
     * @param uri 要检测的Uri
     * @return uri是否指向外部存储
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * 检测是否属于下载uri
     *
     * @param uri 要检测的Uri
     * @return 是否属于下载uri解压完成
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * 检测Uri
     *
     * @param uri Uri
     * @return 是否是多媒体uri
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

}
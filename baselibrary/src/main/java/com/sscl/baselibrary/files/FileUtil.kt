package com.sscl.baselibrary.files

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 文件工具类
 *
 * @author ALM
 */
object FileUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 私有常量 * * * * * * * * * * * * * * * * * * */

    /**
     * 在文件管理器中的程序文件夹名（默认值，未执行初始化函数就是这个值）
     */
    private const val UNNAMED_APP_DIR = "UnnamedAppDir"

    /* * * * * * * * * * * * * * * * * * * 私有可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 初始化状态
     */
    private var init = false

    /**
     * 应用程序名(在文件管理器中的程序文件夹名)
     */
    private var INTERNAL_APP_NAME: String = UNNAMED_APP_DIR

    private var SD_APP_NAME: String = UNNAMED_APP_DIR

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 初始化
     *
     * @param context 上下文
     */
    fun init(context: Context) {
        var filesDir = context.getExternalFilesDir("")
        val parent: String?
        if (filesDir != null) {
            parent = filesDir.parent
            if (parent != null) {
                SD_APP_NAME = parent
            }
        }
        filesDir = context.filesDir
        INTERNAL_APP_NAME = filesDir.absolutePath
        init = true
    }

    /**
     * 获取本项目文件目录
     *
     * @return 本项目文件目录
     */
    val appDir: File?
        get(){
            if (!init) {
                throw NullPointerException("FileUtil not init")
            }
            if (INTERNAL_APP_NAME.isEmpty()) {
                throw ExceptionInInitializerError("File name invalid")
            }
            val file = File(INTERNAL_APP_NAME)
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取本项目在SD卡上的文件目录
     *
     * @return 本项目在SD卡上的文件目录
     */
    val sdCardAppDir: File?
        get() {
            if (!init) {
                throw NullPointerException("FileUtil not init")
            }
            if (SD_APP_NAME.isEmpty()) {
                throw ExceptionInInitializerError("File name invalid")
            }
            val file = File(SD_APP_NAME)
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取项目缓存文件目录
     *
     * @return 项目缓存文件目录
     */
    val cacheDir: File?
        get() {
            val file = File(appDir, "cache")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取项目在SD卡上的缓存文件目录
     *
     * @return 项目在SD卡上的缓存文件目录
     */
    val sdCardCacheDir: File?
        get() {
            val file = File(sdCardAppDir, "cache")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取项目的异常日志目录
     *
     * @return 项目的异常日志目录
     */
    val crashDir: File?
        get() {
            val file = File(appDir, "crash")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取项目在SD卡上的异常日志目录
     *
     * @return 项目在SD卡上的异常日志目录
     */
    val sdCardCrashDir: File?
        get() {
            val file = File(sdCardAppDir, "crash")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取apk存储目录
     *
     * @return apk存储目录
     */
    val apkDir: File?
        get() {
            val file = File(appDir, "apk")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取在SD卡上的apk存储目录
     *
     * @return 在SD卡上的apk存储目录
     */
    val sdCardApkDir: File?
        get() {
            val file = File(sdCardAppDir, "apk")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取图片目录
     *
     * @return 图片目录
     */
    val imageDir: File?
        get() {
            val file = File(appDir, "images")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取在SD卡上的图片目录
     *
     * @return 在SD卡上的图片目录
     */
    val sdCardImageDir: File?
        get() {
            val file = File(sdCardAppDir, "images")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取 Log 日志文件保存目录
     *
     * @return Log 日志文件保存目录
     */
    val sdcardLogInfoDir: File?
        get() {
            val file = File(sdCardAppDir, "logInfo")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }
    val logInfoDir: File?
        get() {
            val file = File(appDir, "logInfo")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取项目数据目录
     *
     * @return 项目数据目录
     */
    val dataDir: File?
        get() {
            val file = File(appDir, "data")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 获取项目在SD卡上的数据目录
     *
     * @return 项目在SD卡上的数据目录
     */
    val sdCardDataDir: File?
        get() {
            val file = File(sdCardAppDir, "data")
            val mkdirs: Boolean
            if (!file.exists()) {
                mkdirs = file.mkdirs()
                return if (mkdirs) {
                    file
                } else {
                    null
                }
            }
            return file
        }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    fun delFile(fileName: String): Boolean {
        val file = File(appDir, fileName)
        if (file.isFile) {
            file.delete()
            return true
        }
        file.exists()
        return false
    }

    /**
     * 保存图片
     *
     * @param bm      位图图片
     * @param picName 文件名
     */
    fun saveBitmap(bm: Bitmap, picName: String) {
        Log.e("", "保存图片")
        try {
            val f = File(cacheDir, "$picName.JPEG")
            if (f.exists()) {
                f.delete()
            }
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Log.e("", "已经保存")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return true表示文件存在
     */
    fun isFileExist(fileName: String): Boolean {
        val file = File(appDir, fileName)
        file.isFile
        return file.exists()
    }

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return true表示文件存在
     */
    fun fileIsExists(path: String): Boolean {
        try {
            val file = File(path)
            if (!file.exists()) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * 删除一个目录
     *
     * @param dir 目录
     */
    fun deleteDirFiles(dir: File?) {
        if (dir == null) {
            return
        }
        if (!dir.exists()) {
            return
        }
        if (!dir.isDirectory) {
            return
        }
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isFile) {
                file.delete() // 删除所有文件
            } else if (file.isDirectory) {
                deleteDirFiles(file) //递规的方式删除文件夹
            }
        }
        dir.delete() // 删除目录本身
    }
}
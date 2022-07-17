package com.sscl.baselibrary.utils

import com.sscl.baselibrary.files.FileUtil
import android.annotation.SuppressLint
import android.text.TextUtils
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author LiangYaLong
 * 描述:
 * 时间:     2020/6/18
 * 版本:     1.0
 */
/**
 * @author Administrator
 *
 *
 * log打印日志保存,文件的保存以小时为单位
 * permission:<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
 * <uses-permission android:name="android.permission.READ_LOGS"></uses-permission>
 */
class LogCatHelper private constructor(path: String) {

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 内部类
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        private class LogRunnable(pid: Int, dirPath: String?) : Runnable {
            private var mProcess: Process? = null
            private var mReader: BufferedReader? = null
            private val cmd: String
            private val mPid: String
            private val dirPath: String?
            override fun run() {
                try {
                    mProcess = Runtime.getRuntime().exec(cmd)
                    mReader = BufferedReader(InputStreamReader(mProcess?.inputStream), 1024)
                    var line: String? = null
                    while ((mReader?.readLine()?.also { line = it }) != null) {
                        if (line == null || line!!.isEmpty()) {
                            continue
                        }
                        saveToFile(line ?: return)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mProcess?.destroy()
                    mProcess = null
                    try {
                        mReader?.close()
                        mReader = null
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }
                }
            }

            private fun saveToFile(line: String) {
                var fileWriter: FileWriter? = null
                try {
                    val file = File(
                        dirPath, getInstance().fileNameStart + FormatDate.getFormatDate(
                            getInstance().fileNameDataPattern
                        ) + getInstance().fileNameExtensionName
                    )
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    if (getInstance().autoDeleteBigFile) {
                        val totalSpace: Long = file.length()
                        val fileMaxSize: Long = getInstance().fileMaxSize
                        //文件大小必须大于0才会生效
                        if (fileMaxSize in 1..totalSpace) {
                            file.delete()
                            file.createNewFile()
                        }
                    }
                    fileWriter = FileWriter(file, true)
                    if (line.contains(mPid)) {
                        fileWriter.append(FormatDate.getFormatTime())
                            .append("	")
                            .append(line)
                            .append("\r\n")
                        fileWriter.flush()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (fileWriter != null) {
                        try {
                            fileWriter.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            init {
                mPid = "" + pid
                this.dirPath = dirPath
                cmd = "logcat *:" + (getInstance()?.logLevel ?: "v") + " | grep \"(" + mPid + ")\""
            }
        }

        @SuppressLint("SimpleDateFormat")
        private object FormatDate {
            fun getFormatDate(fileNameDataPattern: String?): String {
                val sdf = SimpleDateFormat(fileNameDataPattern)
                return sdf.format(System.currentTimeMillis())
            }

            fun getFormatTime(): String {
                return simpleDateFormat.format(System.currentTimeMillis())
            }
        }

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

        /**
         * 时间格式化
         */
        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

        /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

        /**
         * 本类实例
         */
        private var instance: LogCatHelper? = null

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 公开方法
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /**
         * @return 本类单例
         */
        fun getInstance(): LogCatHelper {
            return getInstance(true)
        }

        /**
         * @param useSdcard 是否保存到 SD 卡
         * @return 本类单例
         */
        fun getInstance(useSdcard: Boolean): LogCatHelper {
            var path: String? = null
            if (useSdcard) {
                val logInfoSdcardDir: File? = FileUtil.sdcardLogInfoDir
                if (logInfoSdcardDir != null) {
                    path = logInfoSdcardDir.absolutePath
                }
            } else {
                val logInfoDir: File? = FileUtil.logInfoDir
                if (logInfoDir != null) {
                    path = logInfoDir.absolutePath
                }
            }
            if (path == null) {
                throw NullPointerException("path == null")
            }
            return getInstance(path)
        }

        /**
         * @param path log日志保存根目录
         * @return 本类单例
         */
        fun getInstance(path: String): LogCatHelper {
            if (instance == null) {
                synchronized(LogCatHelper::class.java) {
                    if (instance == null) {
                        instance = LogCatHelper(path)
                    }
                }
            }
            return instance!!
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 应用pid
     */
    private val appid = android.os.Process.myPid()

    private val autoDeleteFileTimerRunnable: Runnable = Runnable {
        //删除崩溃日志
        val file = File(logcatDirectoryPath ?: return@Runnable)
        val files: Array<File>? = file.listFiles()
        if (files != null) {
            for (logFile: File in files) {
                val l: Long = logFile.lastModified()
                val time: Long = System.currentTimeMillis() - l
                //删除大于指定有效期的文件
                if (time > autoDeleteFileTime) {
                    logFile.delete()
                }
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    private var autoDeleteFileTimer: ScheduledExecutorService? = null

    /**
     * 保存路径
     */
    var logcatDirectoryPath: String? = null

    private var logThread: Thread? = null

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 三天
     */
    private var autoDeleteFileTime: Long = (24 * 3 * 60 * 60 * 1000).toLong()

    /**
     * 单个日志文件大小 默认20MB
     */
    private var fileMaxSize: Long = (20 * 1024 * 1024).toLong()

    /**
     * 是否自动删除超过大小的日志文件
     */
    private var autoDeleteBigFile: Boolean = false

    /**
     * 自定义日志记录等级
     */
    private var logLevel: String = "v"

    /**
     * 日志文件的文件名开头
     */
    private var fileNameStart: String = "log-"

    /**
     * 日志文件时间格式化规则
     */
    private var fileNameDataPattern: String = "yyyy-MM-dd-HH"

    /**
     * 文件扩展名
     */
    private var fileNameExtensionName: String = ".log"

    /**
     * 启动log日志保存
     */
    fun init() {
        if (logThread == null) {
            logThread =
                BaseManager.threadFactory.newThread(LogRunnable(appid, logcatDirectoryPath))
        }
        try {
            logThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startAutoDeleteFileTimer()
    }

    /**
     * 设置是否自动删除大文件日志
     *
     * @param autoDeleteBigFile 是否自动删除
     */
    fun setAutoDeleteBigFile(autoDeleteBigFile: Boolean) {
        this.autoDeleteBigFile = autoDeleteBigFile
    }

    fun setFileNameStart(fileNameStart: String) {
        this.fileNameStart = fileNameStart
    }

    /**
     * 设置日志过滤等级 "v" "w" "e" "d" "i"
     *
     * @param logLevel
     */
    fun setLogLevel(logLevel: String) {
        this.logLevel = logLevel
    }

    fun setFileMaxSize(fileMaxSize: Long) {
        this.fileMaxSize = fileMaxSize
    }

    fun setFileNameDataPattern(fileNameDataPattern: String) {
        this.fileNameDataPattern = fileNameDataPattern
    }

    fun setFileNameExtensionName(fileNameExtensionName: String) {
        this.fileNameExtensionName = fileNameExtensionName
    }

    fun setAutoDeleteFileTime(autoDeleteFileTime: Long) {
        this.autoDeleteFileTime = autoDeleteFileTime
    }

    fun destroy() {
        stopAutoDeleteFileTimer()
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 开启自动删除文件的定时器
     */
    private fun startAutoDeleteFileTimer() {
        stopAutoDeleteFileTimer()
        autoDeleteFileTimer = BaseManager.newScheduledExecutorService(1)
        autoDeleteFileTimer?.scheduleAtFixedRate(
            autoDeleteFileTimerRunnable,
            1,
            1,
            TimeUnit.MINUTES
        )
    }

    /**
     * 停止自动删除文件的定时器
     */
    private fun stopAutoDeleteFileTimer() {
        autoDeleteFileTimer?.shutdownNow()
        autoDeleteFileTimer = null
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    init {
        logcatDirectoryPath = if (TextUtils.isEmpty(path)) {
            FileUtil.logInfoDir?.absolutePath
        } else {
            path
        }

        val dir = File(logcatDirectoryPath ?: "")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }
}
package com.sscl.baselibrary.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.Toast
import com.sscl.baselibrary.R
import com.sscl.baselibrary.files.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.system.exitProcess


/**
 * 全局异常捕获工具类
 *
 * @author alm
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

        private val TAG: String = CrashHandler::class.java.simpleName

        /**
         * 用于格式化日期,作为日志文件名的一部分
         */
        private val DATE_FORMAT: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA)

        /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

        /**
         * CrashHandler实例
         */
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CrashHandler? = null

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 公开方法
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /**
         * 获取CrashHandler本类单例
         *
         * @return CrashHandler本类单例
         */
        fun getInstance(): CrashHandler {
            if (INSTANCE == null) {
                synchronized(CrashHandler::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = CrashHandler()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnExceptionListener {
        fun onException(ex: Throwable?)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    private var autoDeleteFileTimer: ScheduledExecutorService? = null

    /**
     * 系统默认的UncaughtException处理类
     */
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * 程序的Context对象
     */
    @SuppressLint("StaticFieldLeak")
    private var mContext: Context? = null

/* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 自动删除文件超时
     */
    private var autoDeleteTime: Long = (24 * 3 * 60 * 60 * 1000).toLong()

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 用来存储设备信息和异常信息
     */
    private val stringStringHashMap: MutableMap<String, String> = HashMap()

    /**
     * 自动删除文件的日志
     */
    private val autoDeleteFileTimerRunnable: Runnable = Runnable {
        //删除崩溃日志
        val file = File(crashFileDirPath ?: return@Runnable)
        val files = file.listFiles()
        if (files != null) {
            for (logFile: File in files) {
                val l: Long = logFile.lastModified()
                val time: Long = System.currentTimeMillis() - l
                //删除大于3天的文件
                if (time > autoDeleteTime) {
                    logFile.delete()
                }
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 存储异常日志的文件目录
     */
    var crashFileDirPath: String? = null
        private set
    private var onExceptionListener: OnExceptionListener? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * @param thread 线程
     * @param ex     Throwable
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                Log.e(TAG, "error : ", e)
            }
            //退出程序
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

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
    @JvmOverloads
    fun init(context: Context, useSdCardDir: Boolean = true) {
        var crashDir: File?
        if (useSdCardDir) {
            crashDir = FileUtil.sdCardCrashDir
            if (crashDir == null) {
                crashDir = FileUtil.crashDir
            }
        } else {
            crashDir = FileUtil.crashDir
        }
        if (crashDir == null) {
            throw NullPointerException("crashDir == null")
        }
        init(context, crashDir.absolutePath)
    }

    /**
     * 自动删除文件的定时器
     *
     * @param autoDeleteTime
     */
    fun setAutoDeleteTime(autoDeleteTime: Long) {
        this.autoDeleteTime = autoDeleteTime
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    fun init(context: Context, crashFileDirPath: String) {
        mContext = context
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
        this.crashFileDirPath = crashFileDirPath
        startAutoDeleteFileTimer()
    }

    fun setOnExceptionListener(onExceptionListener: OnExceptionListener?) {
        this.onExceptionListener = onExceptionListener
    }

    fun destroy() {
        stopAutoDeleteFileTimer()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 错误
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {

        //收集设备参数信息
        collectDeviceInfo(mContext ?: return false)
        //保存日志文件
        saveCrashInfo2File(ex ?: return false)
        if (onExceptionListener != null) {
            val scheduledExecutorService: ScheduledExecutorService =
                BaseManager.newScheduledExecutorService(1)
            scheduledExecutorService.execute { onExceptionListener?.onException(ex) }
        } else {
            val scheduledThreadPoolExecutor: ScheduledExecutorService =
                BaseManager.newScheduledExecutorService(1)
            val runnable = Runnable {
                Looper.prepare()
                Toast.makeText(
                    mContext,
                    R.string.com_jackiepenghe_app_run_with_error,
                    Toast.LENGTH_LONG
                ).show()
                Looper.loop()
            }
            //使用Toast来显示异常信息
            scheduledThreadPoolExecutor.schedule(runnable, 0, TimeUnit.MILLISECONDS)
            BaseManager.handler.postDelayed({ Tool.exitProcess(1) }, 2000)
        }
        return true
    }

    /**
     * 收集设备参数信息
     *
     * @param context 上下文
     */
    private fun collectDeviceInfo(context: Context) {
        try {
            val pm: PackageManager = context.packageManager ?: return
            val pi: PackageInfo =
                pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES) ?: return
            val versionName: String = if (pi.versionName == null) "null" else pi.versionName
            val versionCode: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode.toString()
            } else {
                pi.versionCode.toString()
            }
            stringStringHashMap["versionName"] = versionName
            stringStringHashMap["versionCode"] = versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "an error occurred when collect package info", e)
        }
        val fields: Array<Field> = Build::class.java.declaredFields
        for (field: Field in fields) {
            try {
                field.isAccessible = true
                val o: Any? = field.get(null)
                if (o == null) {
                    stringStringHashMap[field.name] = ""
                    Log.d(TAG, field.name + " : " + o)
                    return
                }
                stringStringHashMap[field.name] = o.toString()
                Log.d(TAG, field.name + " : " + o)
            } catch (e: Exception) {
                Log.e(TAG, "an error occured when collect crash info", e)
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex 异常
     */
    @Synchronized
    private fun saveCrashInfo2File(ex: Throwable) {
        val sb: StringBuilder = StringBuilder()
        for (entry: Map.Entry<String, String> in stringStringHashMap.entries) {
            val key: String = entry.key
            val value: String = entry.value
            sb.append(key).append("=").append(value).append("\n")
        }
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result: String = writer.toString()
        sb.append(result)
        try {
            val timestamp: Long = System.currentTimeMillis()
            val time: String = DATE_FORMAT.format(timestamp)
            val fileName = "crash-$time-$timestamp.log"
            if ((Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)) {
                val dir = File(crashFileDirPath?:return)
                if (!dir.exists()) {
                    val mkdirs: Boolean = dir.mkdirs()
                    Log.e(TAG, "mkdirs $mkdirs")
                }
                val fos = FileOutputStream("$crashFileDirPath/$fileName")
                fos.write(sb.toString().toByteArray())
                fos.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "an error occurred while writing file...", e)
        }
    }

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
}
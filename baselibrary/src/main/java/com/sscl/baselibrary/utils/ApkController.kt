package com.sscl.baselibrary.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.PrintWriter

/**
 * @author jackie
 */
object ApkController {

    private val TAG = ApkController::class.java.simpleName

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 描述: 安装
     */
    fun install(
        apkPath: String,
        context: Context,
        onInstallResultListener: OnInstallResultListener?
    ) {
        DebugUtil.warnOut(TAG, "安装APK apkPath = $apkPath")
        // 先判断手机是否有root权限
        if (SystemUtil.hasRootPermission()) {
            // 有root权限，使用静默安装实现
            silentInstall(apkPath, object : OnInstallResultListener {
                override fun succeed() {
                    DebugUtil.warnOut(TAG, "静默安装成功")
                    onInstallResultListener?.succeed()
                }

                override fun failed() {
                    DebugUtil.warnOut(TAG, "静默安装失败，使用通用安装")
                    standardInstall(context, apkPath, onInstallResultListener)
                }
            })
        } else {
            standardInstall(context, apkPath, onInstallResultListener)
        }
    }

    /**
     * 描述: 卸载
     */
    fun uninstall(packageName: String, context: Context): Boolean {
        return if (SystemUtil.hasRootPermission()) {
            // 有root权限，利用静默卸载实现
            silentUninstall(packageName)
        } else {
            val packageURI = Uri.parse("package:$packageName")
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
            uninstallIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(uninstallIntent)
            true
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 标准安装
     *
     * @param context                 上下文
     * @param apkPath                 APK文件路径
     * @param onInstallResultListener 相关回调
     */
    private fun standardInstall(
        context: Context,
        apkPath: String,
        onInstallResultListener: OnInstallResultListener?
    ) {
        DebugUtil.warnOut(TAG, "标准Intent安装")
        // 没有root权限，利用意图进行安装
        val file = File(apkPath)
        if (!file.exists()) {
            onInstallResultListener?.failed()
            return
        }
        val intent = Intent()
        intent.setAction("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        context.startActivity(intent)
        onInstallResultListener?.succeed()
    }

    /**
     * 静默卸载
     */
    private fun silentUninstall(packageName: String): Boolean {
        val printWriter: PrintWriter
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su")
            printWriter = PrintWriter(process.outputStream)
            printWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ")
            printWriter.println("pm uninstall $packageName")
            printWriter.flush()
            printWriter.close()
            val value = process.waitFor()
            return SystemUtil.returnResult(value)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return false
    }

    /**
     * 静默安装
     */
    private fun silentInstall(apkPath: String, onInstallResultListener: OnInstallResultListener?) {
        DebugUtil.warnOut(TAG, "root 静默安装")
        BaseManager.newThreadFactory().newThread {
            val printWriter: PrintWriter
            var process: Process? = null
            var result: Boolean
            try {
                process = Runtime.getRuntime().exec("su")
                printWriter = PrintWriter(process.outputStream)
                printWriter.println("chmod 777 $apkPath")
                printWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib")
                printWriter.println("pm install -r $apkPath")
                printWriter.println("am start -n com.quicknew.newpackagesave/.ui.activity.guide.SplashActivity")
                printWriter.println("exit")
                printWriter.flush()
                printWriter.close()
                val value = process.waitFor()
                result = SystemUtil.returnResult(value)
            } catch (e: Exception) {
                e.printStackTrace()
                result = false
            } finally {
                process?.destroy()
            }
            if (result) {
                onInstallResultListener?.succeed()
            } else {
                onInstallResultListener?.failed()
            }
        }.start()
    }

    interface OnInstallResultListener {
        /**
         * 成功
         */
        fun succeed()

        /**
         * 失败
         */
        fun failed()
    }
}
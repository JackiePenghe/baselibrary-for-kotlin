package com.sscl.basesample

import android.app.Application
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.sscl.baselibrary.files.FileUtil
import com.sscl.baselibrary.utils.CrashHandler
import com.sscl.baselibrary.utils.CrashHandler.OnExceptionListener
import com.sscl.baselibrary.utils.DebugUtil.setDebugFlag
import com.sscl.baselibrary.utils.DebugUtil.warnOut
import com.sscl.baselibrary.utils.SystemUtil.restartApplication
import com.sscl.baselibrary.utils.Tool.getExceptionDetailsInfo

/**
 * @author alm
 * @date 2017/11/22 0022
 * Application类
 */
class MyApplication : Application() {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    override fun onCreate() {
        super.onCreate()
        myApplication = this
        setDebugFlag(true)
        FileUtil.init(this)

    }

    companion object {
        private var myApplication: MyApplication? = null
        val gSON = Gson()
        private var exceptionDialog: AlertDialog? = null

        fun initCrashListener() {
            CrashHandler.getInstance().setOnExceptionListener(object : OnExceptionListener {
                override fun onException(ex: Throwable?) {
                    if (ex != null) {
                        warnOut("MyApplication", getExceptionDetailsInfo(ex))
                    }
                    restartApplication(myApplication?:return)
                }
            })
            CrashHandler.getInstance().init(myApplication?:return)
        }

        private fun showExceptionUploadingDialog() {
            dismissExceptionUploadingDialog()
            exceptionDialog = AlertDialog.Builder(
                myApplication?:return

            )
                .setMessage("正在收集崩溃信息")
                .setCancelable(false)
                .show()
        }

        private fun dismissExceptionUploadingDialog() {
            if (exceptionDialog != null && exceptionDialog!!.isShowing) {
                exceptionDialog!!.dismiss()
            }
            exceptionDialog = null
        }
    }
}
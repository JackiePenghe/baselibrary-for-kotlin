package com.sscl.basesample;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sscl.baselibrary.files.FileUtil;
import com.sscl.baselibrary.utils.BaseManager;
import com.sscl.baselibrary.utils.CrashHandler;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.basesample.beans.ExceptionUploadBean;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleCallback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

/**
 * @author alm
 * @date 2017/11/22 0022
 * Application类
 */

public class MyApplication extends Application {

    private static MyApplication myApplication;

    private static final Gson GSON = new Gson();

    private static AlertDialog exceptionDialog;

    public static void initCrashListener() {
        CrashHandler.getInstance().setOnExceptionListener(ex -> {
            if (ex != null) {
                DebugUtil.warnOut("MyApplication",Tool.getExceptionDetailsInfo(ex));
            }
            Tool.restartApplication(myApplication);
        });
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        DebugUtil.setDebugFlag(true);
        FileUtil.init(this);
    }

    public static Gson getGSON() {
        return GSON;
    }

    private static void showExceptionUploadingDialog() {
        dismissExceptionUploadingDialog();
        exceptionDialog = new AlertDialog.Builder(myApplication)
                .setMessage("正在收集崩溃信息")
                .setCancelable(false)
                .show();
    }

    private static void dismissExceptionUploadingDialog() {
        if (exceptionDialog != null && exceptionDialog.isShowing()) {
            exceptionDialog.dismiss();
        }
        exceptionDialog = null;
    }

}

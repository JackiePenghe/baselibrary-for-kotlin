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
            String message = ex.getMessage();
            Log.e("MyApplication", "onException: 自己对异常做的额外处理！！！！--------------------\n--------------------\n--------------------\n--------------------\n--------------------\n--------------------");
//            Tool.restartApplication(myApplication);

            Toast.makeText(myApplication, "正在收集错误信息", Toast.LENGTH_LONG).show();

            Kalle.post("http://penghe.xyz/?debug=true")
                    .param("content", message)
                    .perform(new SimpleCallback<String>() {
                        @Override
                        public void onResponse(SimpleResponse<String, String> response) {
                            dismissExceptionUploadingDialog();
                            if (response.isSucceed()) {
                                String succeed = response.succeed();
                                ExceptionUploadBean exceptionUploadBean = null;
                                try {
                                    exceptionUploadBean = GSON.fromJson(succeed, ExceptionUploadBean.class);
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                }
                                if (exceptionUploadBean != null) {
                                    if (exceptionUploadBean.getCode() == 200) {
                                        Toast.makeText(myApplication, "错误信息上传成功", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(myApplication, "错误信息上传失败！" + exceptionUploadBean.getMsg(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(myApplication, "错误信息上传失败！" + exceptionUploadBean.getMsg(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(myApplication, "错误信息上传失败", Toast.LENGTH_LONG).show();
                            }
                            BaseManager.getHandler().postDelayed(() -> Tool.exitProcess(1), 1000);
                        }
                    });
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

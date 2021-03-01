package com.sscl.baselibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.files.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 全局异常捕获工具类
 *
 * @author alm
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 用来存储设备信息和异常信息
     */
    private final Map<String, String> stringStringHashMap = new HashMap<>();

    /*--------------------------------静态常量--------------------------------*/

    private static final String TAG = CrashHandler.class.getSimpleName();

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * CrashHandler实例
     */
    @SuppressLint("StaticFieldLeak")
    private static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 错误
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(@Nullable final Throwable ex) {
        if (ex == null) {
            return false;
        }

        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);


        if (onExceptionListener != null) {
            ScheduledExecutorService scheduledExecutorService = BaseManager.newScheduledExecutorService(1);
            scheduledExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    onExceptionListener.onException(ex);
                    Looper.loop();
                }
            });
        } else {
            ScheduledExecutorService scheduledThreadPoolExecutor = BaseManager.getScheduledThreadPoolExecutor();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, R.string.app_run_with_error, Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            };
            //使用Toast来显示异常信息
            scheduledThreadPoolExecutor.schedule(runnable, 0, TimeUnit.MILLISECONDS);
            BaseManager.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Tool.exitProcess(1);

                }
            }, 2000);
        }
        return true;
    }

    /**
     * 用于格式化日期,作为日志文件名的一部分
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    /**
     * 存储异常日志的文件目录
     */
    private String crashFileDirPath;

    private OnExceptionListener onExceptionListener;

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(@NonNull Context context, boolean useSdCardDir) {
        File crashDir;
        if (useSdCardDir) {
            crashDir = FileUtil.getSdCardCrashDir();
            if (crashDir == null) {
                crashDir = FileUtil.getCrashDir();
            }
        } else {
            crashDir = FileUtil.getCrashDir();
        }
        if (crashDir == null) {
            throw new NullPointerException("crashDir == null");
        }
        init(context, crashDir.getAbsolutePath());
    }

    /*--------------------------------构造函数--------------------------------*/

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /*--------------------------------静态函数--------------------------------*/

    /**
     * 获取CrashHandler本类单例
     *
     * @return CrashHandler本类单例
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (CrashHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    /*--------------------------------实现接口函数--------------------------------*/

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * @param thread 线程
     * @param ex     Throwable
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /*--------------------------------公开函数--------------------------------*/

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(@NonNull Context context) {
        init(context, true);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    @SuppressWarnings("WeakerAccess")
    public void init(@NonNull Context context, @NonNull String crashFileDirPath) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.crashFileDirPath = crashFileDirPath;
    }

    public void setOnExceptionListener(OnExceptionListener onExceptionListener) {
        this.onExceptionListener = onExceptionListener;
    }

    public interface OnExceptionListener {

        void onException(@Nullable Throwable ex);
    }

    /*--------------------------------私有函数--------------------------------*/

    /**
     * 收集设备参数信息
     *
     * @param context 上下文
     */
    private void collectDeviceInfo(@NonNull Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    versionCode = String.valueOf(pi.getLongVersionCode());
                } else {
                    versionCode = pi.versionCode + "";
                }

                stringStringHashMap.put("versionName", versionName);
                stringStringHashMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object o = field.get(null);
                if (o == null) {
                    stringStringHashMap.put(field.getName(), "");
                    Log.d(TAG, field.getName() + " : " + o);
                    return;
                }
                stringStringHashMap.put(field.getName(), o.toString());
                Log.d(TAG, field.getName() + " : " + o);
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex 异常
     */
    private synchronized void saveCrashInfo2File(@NonNull Throwable ex) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : stringStringHashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = DATE_FORMAT.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(crashFileDirPath);
                if (!dir.exists()) {
                    boolean mkdirs = dir.mkdirs();
                    Log.e(TAG, "mkdirs " + mkdirs);
                }
                FileOutputStream fos = new FileOutputStream(crashFileDirPath + "/" + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occurred while writing file...", e);
        }
    }
}

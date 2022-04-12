package com.sscl.baselibrary.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.bean.PhoneInfo;
import com.sscl.baselibrary.receiver.ScreenStatusReceiver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author alm
 */

public class Tool {

    /*--------------------------------静态常量--------------------------------*/

    /**
     * 屏幕状态的监听广播接收者
     */
    private static final ScreenStatusReceiver SCREEN_STATUS_RECEIVER = new ScreenStatusReceiver();

    private static final String IP_V4_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";


    /*--------------------------------公开方法--------------------------------*/

    /**
     * 开始监听屏幕状态
     *
     * @param context 上下文
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Intent startScreenStatusListener(@NonNull Context context) {
        return context.registerReceiver(SCREEN_STATUS_RECEIVER, getScreenStatusReceiverIntentFilter());
    }

    /**
     * 停止监听屏幕状态
     *
     * @param context 上下文
     */
    public static void stopScreenStatusListener(@NonNull Context context) {
        context.unregisterReceiver(SCREEN_STATUS_RECEIVER);
    }

    /**
     * 设置屏幕状态更改的监听
     *
     * @param onScreenStatusChangedListener 屏幕状态更改的监听
     */
    public static void setOnScreenStatusChangedListener(@NonNull ScreenStatusReceiver.OnScreenStatusChangedListener onScreenStatusChangedListener) {
        SCREEN_STATUS_RECEIVER.setOnScreenStatusChangedListener(onScreenStatusChangedListener);
    }

    /**
     * 检测系统环境是否是中文简体
     *
     * @return true表示为中文简体
     */
    public static boolean isZhCn() {
        Locale aDefault = Locale.getDefault();
        String aDefaultStr = aDefault.toString();
        String zhCn = "zh_CN";
        return zhCn.equals(aDefaultStr);
    }

    /**
     * 重启应用程序
     *
     * @param context 上下文
     */
    @SuppressWarnings("unused")
    public static void restartApplication(@NonNull Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) {
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 设置输入框输入类型
     *
     * @param activity  Activity
     * @param editText  输入框
     * @param inputType 输入类型
     */
    public static void setInpType(@NonNull Activity activity, @NonNull EditText editText, int inputType) {

        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        editText.setInputType(inputType);
        try {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            setShowSoftInputOnFocus = cls.getMethod(
                    "setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除输入法的内存泄漏bug
     *
     * @param activity Activity
     */
    @SuppressWarnings("unused")
    public static void releaseInputMethodManagerMemory(@NonNull Activity activity) {
        //解除输入法内存泄漏
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
           if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
               Class<InputMethodManager> inputMethodManagerClass = InputMethodManager.class;
               @SuppressLint({"DiscouragedPrivateApi", "BlockedPrivateApi"}) Field mCurRootViewField = inputMethodManagerClass.getDeclaredField("mCurRootView");
               @SuppressLint("DiscouragedPrivateApi") Field mNextServedViewField = inputMethodManagerClass.getDeclaredField("mNextServedView");
               @SuppressLint("DiscouragedPrivateApi") Field mServedViewField = inputMethodManagerClass.getDeclaredField("mServedView");
               mCurRootViewField.setAccessible(true);
               mNextServedViewField.setAccessible(true);
               mServedViewField.setAccessible(true);
               Object mCurRootView = mCurRootViewField.get(inputMethodManager);
               if (null != mCurRootView) {
                   Context context = ((View) mCurRootView).getContext();
                   if (context == activity) {
                       //将该对象设为null，破环GC引用链，防止输入法内存泄漏
                       mCurRootViewField.set(inputMethodManager, null);
                   }
               }
               Object mNextServedView = mNextServedViewField.get(inputMethodManager);
               if (null != mNextServedView) {
                   Context context = ((View) mNextServedView).getContext();
                   if (activity == context) {
                       mNextServedViewField.set(inputMethodManager, null);
                   }
               }
               Object mServedView = mServedViewField.get(inputMethodManager);
               if (null != mServedView) {
                   Context context = ((View) mServedView).getContext();
                   if (activity == context) {
                       mServedViewField.set(inputMethodManager, null);
                   }
               }
           }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 让当前线程阻塞一段时间
     *
     * @param timeMillis 让线程阻塞的时间（单位：毫秒）
     */
    @SuppressWarnings("unused")
    public static void sleep(long timeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - currentTimeMillis >= timeMillis) {
                break;
            }
        }
    }

    /**
     * 设置文本到粘贴板
     *
     * @param context 上下文
     * @param label   文本说明
     * @param data    文本内容
     * @return true表示设置成功
     */
    @SuppressWarnings("unused")
    public static boolean setDataToClipboard(@NonNull Context context, @NonNull String label, @NonNull String data) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return false;
        }
        ClipData clipData = ClipData.newPlainText(label, data);
        clipboardManager.setPrimaryClip(clipData);
        return true;
    }

    /**
     * 从粘贴板获取文本
     *
     * @param context 上下文
     * @return 粘贴板文本
     */
    @SuppressWarnings("unused")
    public static String getDataFromClipboard(@NonNull Context context) {
        return getDataFromClipboard(context, 0);
    }

    /**
     * 获取系统抛出的异常的详细信息
     *
     * @param throwable 系统抛出的异常
     * @return 系统抛出的异常的详细信息
     */
    public static String getExceptionDetailsInfo(@NonNull Throwable throwable) {
        String message = throwable.getMessage();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        for (StackTraceElement traceElement : stackTrace) {
            stringBuilder.append("\nat ").append(traceElement.toString());
        }
        stringBuilder.append("\n");
        stringBuilder.append("suppressed:\n");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Throwable[] suppressed = throwable.getSuppressed();
            // Print suppressed exceptions, if any
            for (Throwable se : suppressed) {
                for (StackTraceElement traceElement : se.getStackTrace()) {
                    stringBuilder.append("\nat ").append(traceElement.toString());
                }
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append("\n");
        stringBuilder.append("cause:\n");
        Throwable ourCause = throwable.getCause();
        if (ourCause != null) {
            stringBuilder.append(ourCause);
            for (StackTraceElement traceElement : ourCause.getStackTrace()) {
                stringBuilder.append("\nat ").append(traceElement.toString());
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 从粘贴板获取文本
     *
     * @param context 上下文
     * @param index   粘贴板中的文本索引
     * @return 粘贴板文本
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static String getDataFromClipboard(@NonNull Context context, int index) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return null;
        }
        ClipData primaryClip = clipboardManager.getPrimaryClip();
        if (primaryClip == null) {
            return null;
        }
        ClipData.Item itemAt = primaryClip.getItemAt(index);
        return itemAt.getText().toString();
    }

    /**
     * 获取本机信息
     *
     * @param context 上下文
     * @return 本机信息
     */
    @SuppressLint("HardwareIds")
    @SuppressWarnings("unused")
    @Nullable
    public static PhoneInfo getPhoneInfo(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int selfPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if (PackageManager.PERMISSION_GRANTED != selfPermission) {
                return null;
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return null;
        }
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceId = telephonyManager.getImei();
        } else {
            deviceId = telephonyManager.getDeviceId();
        }
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return new PhoneInfo(manufacturer, model, deviceId);
    }

    /**
     * 检查是否符合IPv4格式文本
     *
     * @param ipv4String 字符串
     * @return true表示符合
     */
    @SuppressWarnings("unused")
    public static boolean checkIpv4String(@NonNull String ipv4String) {
        Pattern pattern = Pattern.compile(IP_V4_REGEX);
        Matcher matcher = pattern.matcher(ipv4String);
        return matcher.matches();
    }

    /**
     * 获取RecyclerView第一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    @SuppressWarnings("unused")
    public static int getFirstVisibleItemPosition(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no layoutManager set!");
        }
        if (!(layoutManager instanceof LinearLayoutManager)) {
            throw new IllegalStateException("getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager");
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        return linearLayoutManager.findFirstVisibleItemPosition();
    }

    /**
     * 隐藏导航栏
     *
     * @param activity activity
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void hideNavigationBar(@NonNull Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        //显示NavigationBar
        decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    /**
     * 获取RecyclerView第一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    public static int getFirstCompletelyVisibleItemPosition(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no layoutManager set!");
        }
        if (!(layoutManager instanceof LinearLayoutManager)) {
            throw new IllegalStateException("getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager");
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        return linearLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    /**
     * 获取RecyclerView最后一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    public static int getLastVisibleItemPosition(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no layoutManager set!");
        }
        if (!(layoutManager instanceof LinearLayoutManager)) {
            throw new IllegalStateException("getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager");
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        return linearLayoutManager.findLastVisibleItemPosition();
    }

    /**
     * 获取RecyclerView最后一个完全可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    public static int getLastCompletelyVisibleItemPosition(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no layoutManager set!");
        }
        if (!(layoutManager instanceof LinearLayoutManager)) {
            throw new IllegalStateException("getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager");
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        return linearLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    /**
     * 设置软键状态
     *
     * @param activity 上下文
     * @param show     是否显示
     */
    public static void setInputMethodState(@NonNull Activity activity, boolean show) {
        setInputMethodState(activity, show, false);
    }

    /**
     * 设置软键状态
     *
     * @param activity 上下文
     * @param show     是否显示
     */
    public static void setInputMethodState(@NonNull Activity activity, boolean show, boolean needFocus) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (null == imm) {
            return;
        }

        if (show) {
            if (activity.getCurrentFocus() != null) {
                //有焦点打开
                imm.showSoftInput(activity.getCurrentFocus(), 0);
            } else {
                if (!needFocus) {
                    //无焦点打开
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        } else {
            if (activity.getCurrentFocus() != null) {
                //有焦点关闭
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                if (!needFocus) {
                    //无焦点关闭
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        }
    }

    /**
     * 转换DP值为象素值
     *
     * @param dp DP值
     * @return 象素值
     */
    public static double dpToPx(double dp) {
        return Resources.getSystem().getDisplayMetrics().density * dp;
    }

    /**
     * 获取主题Primary颜色
     *
     * @param context 上下文
     * @return 主题Primary颜色
     */
    public static int getColorPrimary(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }


    /**
     * 获取暗色主题Primary颜色
     *
     * @param context 上下文
     * @return 暗色主题Primary颜色
     */
    public static int getDarkColorPrimary(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }


    /**
     * 获取暗色主题Primary颜色
     *
     * @param context 上下文
     * @return 暗色主题Primary颜色
     */
    public static int getDarkColorPrimaryVariant(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取主题Accent颜色
     *
     * @param context 上下文
     * @return 主题Accent颜色
     */
    public static int getColorAccent(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取主题状态栏颜色
     *
     * @param context 上下文
     * @return 主题状态栏颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getStatusBarColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.statusBarColor, typedValue, true);
        return typedValue.data;
    }

    public static void exitProcess(int status) {
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(status);
    }

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 获取监听屏幕状态的广播接收者
     *
     * @return 监听屏幕状态的广播接收者
     */
    private static IntentFilter getScreenStatusReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(Integer.MAX_VALUE);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        return intentFilter;
    }
}

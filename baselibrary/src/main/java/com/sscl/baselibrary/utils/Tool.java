package com.sscl.baselibrary.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sscl.baselibrary.bean.PhoneInfo;
import com.sscl.baselibrary.receiver.ScreenStatusReceiver;

import java.lang.reflect.Field;
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
    @SuppressWarnings("WeakerAccess")
    public static boolean isZhCn() {
        Locale aDefault = Locale.getDefault();
        String aDefaultStr = aDefault.toString();
        String zhCn = "zh_CN";
        return zhCn.equals(aDefaultStr);
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
            @SuppressWarnings("JavaReflectionMemberAccess") Field mCurRootViewField = InputMethodManager.class.getDeclaredField("mCurRootView");
            @SuppressWarnings("JavaReflectionMemberAccess") Field mNextServedViewField = InputMethodManager.class.getDeclaredField("mNextServedView");
            @SuppressWarnings("JavaReflectionMemberAccess") Field mServedViewField = InputMethodManager.class.getDeclaredField("mServedView");
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
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
    @SuppressWarnings("unused")
    @Nullable
    @SuppressLint("HardwareIds")
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
        String phoneImei;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneImei = telephonyManager.getImei();
        } else {
            phoneImei = telephonyManager.getDeviceId();
        }
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return new PhoneInfo(manufacturer, model, phoneImei);
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
     * 获取RecyclerView第一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
     * 获取RecyclerView最后一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    @SuppressWarnings("unused")
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

package com.sscl.baselibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 自定义Toast，可实现自定义显示时间,兼容至安卓N及以上版本
 */
class CustomToast {

    /**
     * 长时间的吐司持续时间
     */
    static final int LENGTH_LONG = 3500;
    /**
     * 短时间的吐司持续时间
     */
    static final int LENGTH_SHORT = 2000;

    /*--------------------------------静态变量--------------------------------*/

    /**
     * 自定义吐司本类单例
     */
    @SuppressLint("StaticFieldLeak")
    private static CustomToast customToast;

    /**
     * 吐司专用的handler(使用Handler可以避免定时器在非主线程中导致的线程问题)
     */
    @SuppressLint("StaticFieldLeak")
    private static ToastHandler toastHandler;
    /**
     * 是否重用上次未消失的Toast的标志（缓存标志），实际标志在handler中
     */
    private static boolean reuse = false;
    /**
     * showToast的定时任务
     */
    private static ScheduledExecutorService SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE;
    /**
     * hideToast的定时任务
     */
    private static ScheduledExecutorService HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE;

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 上下文
     */
    private final Context context;
    /**
     * Toast文本内容
     */
    private String messageText;
    /**
     * Toast持续时长
     */
    private int duration;
    /**
     * View视图
     */
    private View view;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param context     上下文
     * @param messageText Toast文本内容
     * @param duration    Toast持续时间（单位：毫秒）
     */
    private CustomToast(@NonNull Context context, @NonNull String messageText, int duration) {
        this.context = context;
        this.messageText = messageText;
        this.duration = duration;
    }

    private CustomToast(Context context, View view, int duration) {
        this.context = context;
        this.view = view;
        this.duration = duration;
    }

    /*--------------------------------私有静态方法--------------------------------*/

    /**
     * 显示Toast
     *
     * @param context     上下文
     * @param messageText Toast文本内容
     * @param duration    Toast持续时间（单位：毫秒）
     */
    @SuppressLint("ShowToast")
    private static void showMyToast(@NonNull final Context context, @NonNull final String messageText, int duration) {
        if (toastHandler == null) {
            toastHandler = new ToastHandler(context);
        }else {
            toastHandler.setContext(context);
        }
        setHandlerReuse();
        if (SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE != null) {
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
        }

        if (HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE != null) {
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
        }
        SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2);
        HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2);
        final boolean[] first = {true};
        SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (first[0]) {
                        handlerShowToast(messageText, ToastHandler.FIRST_SEND);
                        first[0] = false;
                    } else {
                        handlerShowToast(messageText, ToastHandler.KEEP_TOAST);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000, TimeUnit.MILLISECONDS);

        HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
                    handlerCancelToast();
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, duration, TimeUnit.MILLISECONDS);
    }

    /**
     * 显示Toast
     *
     * @param context  上下文
     * @param view     View
     * @param duration Toast持续时间（单位：毫秒）
     */
    @SuppressLint("ShowToast")
    private static void showMyToast(@NonNull final Context context, @NonNull final View view, int duration) {
        if (toastHandler == null) {
            toastHandler = new ToastHandler(context);
        }else {
            toastHandler.setContext(context);
        }
        setHandlerReuse();
        if (SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE != null) {
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
        }

        if (HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE != null) {
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
        }
        SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2);
        HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2);
        final boolean[] first = {true};
        SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (first[0]) {
                        handlerShowToast(view, ToastHandler.FIRST_SEND);
                        first[0] = false;
                    } else {
                        handlerShowToast(view, ToastHandler.KEEP_TOAST);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000, TimeUnit.MILLISECONDS);

        HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
                    handlerCancelToast();
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, duration, TimeUnit.MILLISECONDS);
    }

    /**
     * 使用Handler显示Toast
     *
     * @param messageText Toast文本内容
     * @param arg         是否为定时器保持消息显示
     */
    private static void handlerShowToast(@NonNull String messageText, int arg) {
        Message message = new Message();
        message.obj = messageText;
        message.what = ToastHandler.MESSAGE;
        message.arg1 = arg;
        toastHandler.sendMessage(message);
    }

    /**
     * 使用Handler显示Toast
     *
     * @param arg 是否为定时器保持消息显示
     */
    private static void handlerShowToast(@NonNull View view, int arg) {
        Message message = new Message();
        message.obj = view;
        message.what = ToastHandler.VIEW;
        message.arg1 = arg;
        toastHandler.sendMessage(message);
    }

    /**
     * 设置Handler是否重用未消失的Toast
     */
    private static void setHandlerReuse() {
        Message message = new Message();
        message.what = ToastHandler.SET_RE_USE;
        message.obj = reuse;
        toastHandler.sendMessage(message);
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 显示吐司
     */
    void show() {
        if (view == null) {
            showMyToast(context, messageText, duration);
        } else {
            showMyToast(context, view, duration);
        }
    }

    /**
     * 设置是否重用（缓存位，每次在显示Toast前会将其设置到Handler中）
     *
     * @param reuse true表示开启重用
     */
    static void setReuse(boolean reuse) {
        CustomToast.reuse = reuse;
    }

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 获取CustomToast本类
     *
     * @param context  上下文
     * @param message  吐司显示信息
     * @param duration 吐司显示时长
     * @return CustomToast本类
     */
    @NonNull
    static CustomToast makeText(@NonNull Context context, @NonNull String message, int duration) {
        if (customToast == null) {
            synchronized (CustomToast.class) {
                if (customToast == null) {
                    customToast = new CustomToast(context, message, duration);
                } else {
                    customToast.messageText = message;
                    customToast.duration = duration;
                }
            }
        } else {
            customToast.messageText = message;
            customToast.duration = duration;
        }
        return customToast;
    }

    /**
     * 使用Handler取消Toast
     */
    static void handlerCancelToast() {
        try {
            Message message = new Message();
            message.what = ToastHandler.CANCEL;
            toastHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CustomToast本类
     *
     * @param context    上下文
     * @param messageRes 吐司显示信息
     * @param duration   吐司显示时长
     * @return CustomToast本类
     */
    @NonNull
    static CustomToast makeText(@NonNull Context context, @StringRes int messageRes, int duration) {
        String message = context.getString(messageRes);
        if (customToast == null) {
            synchronized (CustomToast.class) {
                if (customToast == null) {
                    customToast = new CustomToast(context, message, duration);
                } else {
                    customToast.messageText = message;
                    customToast.view = null;
                    customToast.duration = duration;
                }
            }
        } else {
            customToast.messageText = message;
            customToast.view = null;
            customToast.duration = duration;
        }
        return customToast;
    }

    @NonNull
    static CustomToast makeText(Context context,@NonNull View view, int duration) {
        if (customToast == null) {
            synchronized (CustomToast.class) {
                if (customToast == null) {
                    customToast = new CustomToast(context, view, duration);
                } else {
                    customToast.view = view;
                    customToast.duration = duration;
                }
            }
        } else {
            customToast.view = view;
            customToast.duration = duration;
        }
        return customToast;
    }
}

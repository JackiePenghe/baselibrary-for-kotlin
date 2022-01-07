package com.sscl.baselibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * 自定义Toast专用的Handler
 */
class ToastHandler extends Handler {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 库内静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 显示Toast
     */
    static final int MESSAGE = 1;
    /**
     * 取消toast显示
     */
    static final int CANCEL = 2;
    /**
     * 设置是否重用上次未消失的Toast直接进行显示
     */
    static final int SET_RE_USE = 3;
    /**
     * 当前是否是用于保持Toast显示（超过3000秒时长的Toast）
     */
    static final int KEEP_TOAST = 4;
    /**
     * 当前是否为第一次弹出Toast
     */
    static final int FIRST_SEND = 5;
    /**
     * 使用视图
     */
    static final int VIEW = 6;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * Toast实例
     */
    private Toast textToast;
    private Toast viewToast;
    /**
     * 是否重用上次还未消失的Toast
     */
    private static boolean reuse = false;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 上下文
     */
    private Context context;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Default constructor associates this handler with the {@link Looper} for the
     * current thread.
     * <p>
     * If this thread does not have a looper, this handler won't be able to receive messages
     * so an exception is thrown.
     */
    ToastHandler(@NonNull Context context) {
        super(Looper.getMainLooper());
        this.context = context;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void setContext(Context context) {
        this.context = context;
    }

    /*--------------------------------重写父类方法--------------------------------*/

    /**
     * Subclasses must implement this to receive messages.
     *
     * @param msg 信息
     */
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        int what = msg.what;
        switch (what) {
            case MESSAGE:
            case VIEW:
                showToast(msg);
                break;
            case CANCEL:
                hideToast();
                break;
            case SET_RE_USE:
                setReuse(msg);
                break;
            default:
                break;
        }
    }

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 设置是否重用未消失的Toast
     *
     * @param msg Message消息
     */
    private void setReuse(@NonNull Message msg) {
        Object obj = msg.obj;
        if (obj == null) {
            return;
        }
        if (!(obj instanceof Boolean)) {
            return;
        }
        reuse = (boolean) obj;
    }

    /**
     * 隐藏Toast
     */
    private void hideToast() {
        if (textToast != null) {
            textToast.cancel();
            textToast = null;
        }
    }

    /**
     * 显示Toast
     *
     * @param msg Message消息
     */
    @SuppressLint("ShowToast")
    private void showToast(Message msg) {
        Object obj = msg.obj;
        int arg1 = msg.arg1;
        if (obj == null) {
            return;
        }
        if (obj instanceof String) {
            if (viewToast != null){
                viewToast.cancel();
                viewToast = null;
            }
            String messageText = (String) obj;
            if (textToast == null) {
                textToast = Toast.makeText(context, messageText, Toast.LENGTH_LONG);
            } else {
                if (reuse) {
                    textToast.setText(messageText);
                } else {
                    if (arg1 != KEEP_TOAST) {
                        hideToast();
                    }
                    textToast = Toast.makeText(context, messageText, Toast.LENGTH_LONG);
                }
            }
            textToast.show();
        } else if (obj instanceof View) {
            if (textToast != null){
                textToast.cancel();
                textToast = null;
            }
            View view = (View) obj;
            if (viewToast == null) {
                viewToast = new Toast(context);
            } else {
                if (!reuse) {
                    if (arg1 != KEEP_TOAST) {
                        hideToast();
                    }
                    viewToast = new Toast(context);
                }
            }
            viewToast.setView(view);
            viewToast.show();
        } else {
            return;
        }
    }

    static boolean isReuse() {
        return reuse;
    }
}

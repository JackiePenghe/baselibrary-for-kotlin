package com.sscl.baselibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 可以自动触发下拉刷新的控件
 *
 * @author jackie
 */
public class AutoSwipeRefreshLayout extends SwipeRefreshLayout {

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     * @param context 上下文
     */
    public AutoSwipeRefreshLayout(@NonNull Context context) {
        this(context,null);
    }

    /**
     * 构造方法
     * @param context 上下文
     * @param attrs 控件属性
     */
    public AutoSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 自动刷新
     */
    @SuppressWarnings("unused")
    public void autoRefresh() {
        try {
            Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");
            mCircleView.setAccessible(true);
            View progress = (View) mCircleView.get(this);
            if (progress != null) {
                progress.setVisibility(VISIBLE);
            }

            Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);
            setRefreshing.invoke(this, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
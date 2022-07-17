package com.sscl.baselibrary.widget

import android.content.*
import android.util.AttributeSet
import android.view.*
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

import kotlin.jvm.JvmOverloads
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 可以自动触发下拉刷新的控件
 *
 * @author jackie
 */
class AutoSwipeRefreshLayout
/**
 * 构造方法
 * @param context 上下文
 * @param attrs 控件属性
 */
/*--------------------------------构造方法--------------------------------*/
/**
 * 构造方法
 * @param context 上下文
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SwipeRefreshLayout(context, attrs) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 自动刷新
     */
    fun autoRefresh() {
        try {
            val mCircleView: Field = SwipeRefreshLayout::class.java.getDeclaredField("mCircleView")
            mCircleView.isAccessible = true
            (mCircleView.get(this) as View?)?.visibility = VISIBLE
            val setRefreshing: Method = SwipeRefreshLayout::class.java.getDeclaredMethod(
                "setRefreshing",
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType
            )
            setRefreshing.isAccessible = true
            setRefreshing.invoke(this, true, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.sscl.baselibrary.utils

import android.app.Activity
import com.sscl.baselibrary.R
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.annotation.ColorRes
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.LinearLayout
import android.view.ViewGroup.MarginLayoutParams
import android.content.*
import android.graphics.*
import android.view.*
import androidx.annotation.IntRange

/**
 * 状态栏颜色设置工具类
 *
 * @author jacke
 */
object StatusBarUtil {
    /*--------------------------------静态常量--------------------------------*/
    val DEFAULT_STATUS_BAR_ALPHA: Int = 112
    private val FAKE_STATUS_BAR_VIEW_ID: Int =
        R.id.com_jackiepenghe_activity_statusbarutil_fake_status_bar_view
    private val FAKE_TRANSLUCENT_VIEW_ID: Int =
        R.id.com_jackiepenghe_activity_statusbarutil_translucent_view
    private val TAG_KEY_HAVE_SET_OFFSET: Int = -123
    private val TAG: String = StatusBarUtil::class.java.getSimpleName()
    /*--------------------------------公开静态方法--------------------------------*/
    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    fun setColor(activity: Activity, @ColorInt color: Int) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    @kotlin.jvm.JvmStatic
    fun setColor(
        activity: Activity,
        @ColorInt color: Int,
        @IntRange(from = 0, to = 255) statusBarAlpha: Int
    ) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            //此参数在API30被弃用
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha))
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    fun setColorForSwipeBack(activity: Activity, color: Int) {
        setColorForSwipeBack(activity, color, DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    fun setColorForSwipeBack(
        activity: Activity, @ColorInt color: Int,
        @IntRange(from = 0, to = 255) statusBarAlpha: Int
    ) {
        val contentView: ViewGroup = activity.findViewById(android.R.id.content)
        val rootView: View = contentView.getChildAt(0)
        val statusBarHeight: Int = getStatusBarHeight(activity)
        if (rootView is CoordinatorLayout) {
            rootView.setStatusBarBackgroundColor(calculateStatusColor(color, statusBarAlpha))
        } else {
            contentView.setPadding(0, statusBarHeight, 0, 0)
            contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
        }
        setTransparentForWindow(activity)
    }

    /**
     * 设置状态栏纯色 不加半透明效果
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    fun setColorNoTranslucent(activity: Activity, @ColorInt color: Int) {
        setColor(activity, color, 0)
    }

    fun setColorNoTranslucentRes(activity: Activity, @ColorRes colorRes: Int) {
        val color: Int = ContextCompat.getColor(activity, colorRes)
        setColorNoTranslucent(activity, color)
    }

    /**
     * 设置状态栏颜色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    @Deprecated("")
    fun setColorDiff(activity: Activity, @ColorInt color: Int) {
        transparentStatusBar(activity)
        val contentView: ViewGroup = activity.findViewById(android.R.id.content)
        // 移除半透明矩形,以免叠加
        val fakeStatusBarView: View? = contentView.findViewById(FAKE_STATUS_BAR_VIEW_ID)
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE)
            }
            fakeStatusBarView.setBackgroundColor(color)
        } else {
            contentView.addView(createStatusBarView(activity, color))
        }
        setRootView(activity)
    }

    /**
     * 使状态栏半透明
     *
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    fun setTranslucent(activity: Activity) {
        setTranslucent(activity, DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 使状态栏半透明
     *
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    fun setTranslucent(activity: Activity, @IntRange(from = 0, to = 255) statusBarAlpha: Int) {
        setTransparent(activity)
        addTranslucentView(activity, statusBarAlpha)
    }

    /**
     * 针对根布局是 CoordinatorLayout, 使状态栏半透明
     *
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    fun setTranslucentForCoordinatorLayout(
        activity: Activity,
        @IntRange(from = 0, to = 255) statusBarAlpha: Int
    ) {
        transparentStatusBar(activity)
        addTranslucentView(activity, statusBarAlpha)
    }

    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    fun setTransparent(activity: Activity) {
        transparentStatusBar(activity)
        setRootView(activity)
    }

    /**
     * 使状态栏透明(5.0以上半透明效果,不建议使用)
     *
     *
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    @Deprecated("")
    fun setTranslucentDiff(activity: Activity) {
        // 设置状态栏透明
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setRootView(activity)
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    fun setColorForDrawerLayout(
        activity: Activity,
        drawerLayout: DrawerLayout,
        @ColorInt color: Int
    ) {
        setColorForDrawerLayout(activity, drawerLayout, color, DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 为DrawerLayout 布局设置状态栏颜色,纯色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    fun setColorNoTranslucentForDrawerLayout(
        activity: Activity,
        drawerLayout: DrawerLayout,
        @ColorInt color: Int
    ) {
        setColorForDrawerLayout(activity, drawerLayout, color, 0)
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity       需要设置的activity
     * @param drawerLayout   DrawerLayout
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    fun setColorForDrawerLayout(
        activity: Activity, drawerLayout: DrawerLayout, @ColorInt color: Int,
        @IntRange(from = 0, to = 255) statusBarAlpha: Int
    ) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            //此参数在API30被弃用
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT)
        // 生成一个状态栏大小的矩形
        // 添加 statusBarView 到布局中
        val contentLayout: ViewGroup = drawerLayout.getChildAt(0) as ViewGroup
        val fakeStatusBarView: View? = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID)
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE)
            }
            fakeStatusBarView.setBackgroundColor(color)
        } else {
            contentLayout.addView(createStatusBarView(activity, color), 0)
        }
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout is LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1)
                .setPadding(
                    contentLayout.getPaddingLeft(),
                    getStatusBarHeight(activity) + contentLayout.getPaddingTop(),
                    contentLayout.getPaddingRight(),
                    contentLayout.getPaddingBottom()
                )
        }
        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout)
        addTranslucentView(activity, statusBarAlpha)
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    @Deprecated("")
    fun setColorForDrawerLayoutDiff(
        activity: Activity,
        drawerLayout: DrawerLayout,
        @ColorInt color: Int
    ) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // 生成一个状态栏大小的矩形
        val contentLayout: ViewGroup = drawerLayout.getChildAt(0) as ViewGroup
        val fakeStatusBarView: View? = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID)
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE)
            }
            fakeStatusBarView.setBackgroundColor(
                calculateStatusColor(
                    color,
                    DEFAULT_STATUS_BAR_ALPHA
                )
            )
        } else {
            // 添加 statusBarView 到布局中
            contentLayout.addView(createStatusBarView(activity, color), 0)
        }
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout is LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0)
        }
        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout)
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    fun setTranslucentForDrawerLayout(activity: Activity, drawerLayout: DrawerLayout) {
        setTranslucentForDrawerLayout(activity, drawerLayout, DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    fun setTranslucentForDrawerLayout(
        activity: Activity, drawerLayout: DrawerLayout,
        @IntRange(from = 0, to = 255) statusBarAlpha: Int
    ) {
        setTransparentForDrawerLayout(activity, drawerLayout)
        addTranslucentView(activity, statusBarAlpha)
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    fun setTransparentForDrawerLayout(activity: Activity, drawerLayout: DrawerLayout) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            //此参数在API30被弃用
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT)
        val contentLayout: ViewGroup = drawerLayout.getChildAt(0) as ViewGroup
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout is LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0)
        }

        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout)
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明(5.0以上半透明效果,不建议使用)
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    @Deprecated("")
    fun setTranslucentForDrawerLayoutDiff(activity: Activity, drawerLayout: DrawerLayout) {
        // 设置状态栏透明
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // 设置内容布局属性
        val contentLayout: ViewGroup = drawerLayout.getChildAt(0) as ViewGroup
        contentLayout.setFitsSystemWindows(true)
        contentLayout.setClipToPadding(true)
        // 设置抽屉布局属性
        val vg: ViewGroup = drawerLayout.getChildAt(1) as ViewGroup
        vg.setFitsSystemWindows(false)
        // 设置 DrawerLayout 属性
        drawerLayout.setFitsSystemWindows(false)
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏全透明
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTransparentForImageView(activity: Activity, needOffsetView: View) {
        setTranslucentForImageView(activity, 0, needOffsetView)
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageView(activity: Activity, needOffsetView: View) {
        setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView)
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageView(
        activity: Activity, @IntRange(from = 0, to = 255) statusBarAlpha: Int,
        needOffsetView: View?
    ) {
        setTransparentForWindow(activity)
        addTranslucentView(activity, statusBarAlpha)
        if (needOffsetView != null) {
            val haveSetOffset: Any? = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET)
            if (haveSetOffset != null && haveSetOffset as Boolean) {
                return
            }
            val layoutParams: MarginLayoutParams =
                needOffsetView.getLayoutParams() as MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(activity),
                layoutParams.rightMargin, layoutParams.bottomMargin
            )
            needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true)
        }
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageViewInFragment(activity: Activity, needOffsetView: View) {
        setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView)
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTransparentForImageViewInFragment(activity: Activity, needOffsetView: View) {
        setTranslucentForImageViewInFragment(activity, 0, needOffsetView)
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageViewInFragment(
        activity: Activity, @IntRange(from = 0, to = 255) statusBarAlpha: Int,
        needOffsetView: View
    ) {
        setTranslucentForImageView(activity, statusBarAlpha, needOffsetView)
    }
    /*--------------------------------私有静态方法--------------------------------*/
    /**
     * 设置 DrawerLayout 属性
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout DrawerLayout 的内容布局
     */
    private fun setDrawerLayoutProperty(
        drawerLayout: DrawerLayout,
        drawerLayoutContentLayout: ViewGroup
    ) {
        val drawer: ViewGroup = drawerLayout.getChildAt(1) as ViewGroup
        drawerLayout.setFitsSystemWindows(false)
        drawerLayoutContentLayout.setFitsSystemWindows(false)
        drawerLayoutContentLayout.setClipToPadding(true)
        drawer.setFitsSystemWindows(false)
    }

    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private fun addTranslucentView(
        activity: Activity,
        @IntRange(from = 0, to = 255) statusBarAlpha: Int
    ) {
        val contentView: ViewGroup = activity.findViewById(android.R.id.content)
        val fakeTranslucentView: View? = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID)
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE)
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0))
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha))
        }
    }
    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    /**
     * 生成一个和状态栏大小相同的彩色矩形条
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private fun createStatusBarView(
        activity: Activity,
        @ColorInt color: Int,
        alpha: Int = 0
    ): View {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView: View = View(activity)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(activity)
        )
        statusBarView.setLayoutParams(params)
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID)
        return statusBarView
    }

    /**
     * 设置根布局参数
     */
    private fun setRootView(activity: Activity) {
        val parent: ViewGroup = activity.findViewById(android.R.id.content)
        var i: Int = 0
        val count: Int = parent.getChildCount()
        while (i < count) {
            val childView: View = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(true)
                childView.setClipToPadding(true)
            }
            i++
        }
    }

    /**
     * 设置透明
     */
    private fun setTransparentForWindow(activity: Activity) {
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT)
        val decorView: View = activity.getWindow()
            .getDecorView()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            //android API 30以上的版本,setSystemUiVisibility被弃用了，使用WindowInsetsController代替
            //WindowInsetsController windowInsetsController = decorView.getWindowInsetsController();
            //windowInsetsController.show(WindowInsets.Type.statusBars());
            //android API 30以上的版本，SYSTEM_UI_FLAG_LAYOUT_STABLE 被弃用了，只能使用WindowInsets#getInsetsIgnoringVisibility(int)查看相关状态，但不可以更改
            //android API 30以上的版本，SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 被弃用了，只能使用WindowInsets#getInsetsIgnoringVisibility(int)查看相关状态，但不可以更改
            //decorView.getRootWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.statusBars());
            DebugUtil.warnOut(
                TAG,
                "setTransparentForWindow:android API 30以上的版本,setSystemUiVisibility被弃用了，请使用WindowInsetsController代替"
            )
        }
    }

    /**
     * 使状态栏透明
     */
    private fun transparentStatusBar(activity: Activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            //此参数在API30被弃用
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT)
    }

    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private fun createTranslucentStatusBarView(activity: Activity, alpha: Int): View {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView: View = View(activity)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(activity)
        )
        statusBarView.setLayoutParams(params)
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
        statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID)
        return statusBarView
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId: Int =
            context.getResources().getIdentifier("status_bar_height", "dimen", "android")
        return context.getResources().getDimensionPixelSize(resourceId)
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val a: Float = 1 - alpha / 255f
        var red: Int = color shr 16 and 0xff
        var green: Int = color shr 8 and 0xff
        var blue: Int = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return Color.rgb(red, green, blue)
    }
}
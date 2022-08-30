package com.sscl.baselibrary.activity

import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import java.lang.Exception
import java.lang.RuntimeException

import com.sscl.baselibrary.R
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.annotation.StringRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.drawerlayout.widget.DrawerLayout
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.annotation.IdRes
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.annotation.MenuRes

/**
 * 带有侧边栏的Activity基类
 *
 * @author alm
 */
abstract class BaseDrawerActivity : AppCompatActivity() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    /**
     * 抽屉布局侧边栏的相关监听
     */
    private val drawerListener: DrawerListener = object : DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            drawerSlide(drawerView, slideOffset)
        }

        override fun onDrawerOpened(drawerView: View) {
            drawerOpened(drawerView)
            isDrawerOpen = true
        }

        override fun onDrawerClosed(drawerView: View) {
            drawerClosed(drawerView)
            isDrawerOpen = false
            if (menuItemId != -1) {
                navigationItemSelected(menuItemId)
                menuItemId = -1
            }
        }

        override fun onDrawerStateChanged(newState: Int) {
            when (newState) {
                DrawerLayout.STATE_DRAGGING, DrawerLayout.STATE_SETTLING -> isDrawerOpen = true
                DrawerLayout.STATE_IDLE -> {}
                else -> {}
            }
            drawerStateChanged(newState)
        }
    }

    /**
     * 侧边栏的选项点击监听
     */
    private val onNavigationItemSelectedListener: NavigationView.OnNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            menuItemId = item.itemId
            closeDrawer()
            true
        }

    /* * * * * * * * * * * * * * * * * * * 延时初始化属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 标题
     */
    private lateinit var toolbar: Toolbar

    /**
     * 抽屉布局
     */
    private lateinit var drawerLayout: DrawerLayout

    /**
     * 左上角可展示侧边栏状态的小控件
     */
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    /**
     * 侧边栏
     */
    private lateinit var navigationView: NavigationView

    /**
     * 标题文字
     */
    private lateinit var titleView: TextView

    /**
     * 子类继承此类之后，设置的布局都在FrameLayout中
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var contentView: FrameLayout
        private set

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 侧边栏被选中的ID
     */
    private var menuItemId: Int = 0

    /**
     * 记录当前侧边栏的打开状态
     */
    protected var isDrawerOpen: Boolean = false
        private set


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBeforeSetLayout()
        setContentView(R.layout.com_jackiepenghe_baselibrary_activity_base_drawer)
        toolbar = findViewById(R.id.toolbar)
        titleView = findViewById(R.id.toolbar_title)
        titleView.setText(R.string.app_name)
        setSupportActionBar(toolbar)
        val supportActionBar: ActionBar? = supportActionBar
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar.setDisplayShowTitleEnabled(false)
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.com_jackiepenghe_navigation_drawer_open,
            R.string.com_jackiepenghe_navigation_drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        drawerLayout.addDrawerListener(drawerListener)
        actionBarDrawerToggle.syncState()
        navigationView = findViewById(R.id.nav_view)
        val headerView: View = try {
            View.inflate(
                this@BaseDrawerActivity,
                setNavigationViewHeaderViewLayoutResId(),
                null
            )
        } catch (e: Exception) {
            View.inflate(this@BaseDrawerActivity, R.layout.nav_header_main, null)
        }
        navigationView.addHeaderView(headerView)
        try {
            navigationView.inflateMenu(setNavigationMenuResId())
        } catch (e: Exception) {
            navigationView.inflateMenu(R.menu.com_jackiepenghe_activity_main_drawer)
        }
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)
        contentView = findViewById(R.id.base_frame_content)
        val layoutResId: Int = setLayout()
        if (layoutResId == 0) {
            throw RuntimeException("setLayout with wrong layout resource id")
        }
        val view: View = layoutInflater.inflate(layoutResId, null)
        contentView.addView(view)
        doBeforeInitOthers()
        initViews()
        initViewData()
        initOtherData()
        initEvents()
        doAfterAll()
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar.setNavigationOnClickListener(null)
        toolbar.navigationIcon = null
        drawerLayout.removeAllViews()
        drawerLayout.removeDrawerListener(drawerListener)
        drawerLayout.removeDrawerListener(actionBarDrawerToggle)
        navigationView.setNavigationItemSelectedListener(null)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @LayoutRes
    abstract fun setLayout(): Int

    /**
     * 在设置布局之前需要进行的操作
     */
    abstract fun doBeforeSetLayout()

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    abstract fun doBeforeInitOthers()

    /**
     * 初始化布局控件
     */
    abstract fun initViews()

    /**
     * 初始化控件数据
     */
    abstract fun initViewData()

    /**
     * 初始化其他数据
     */
    abstract fun initOtherData()

    /**
     * 初始化事件
     */
    abstract fun initEvents()

    /**
     * 在最后执行的操作
     */
    abstract fun doAfterAll()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 子类可用方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 关闭侧边栏
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleResId 标题栏的资源id
     */
    protected fun setTitleText(@StringRes titleResId: Int) {
        titleView.setText(titleResId)
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏内容
     */
    protected fun setTitleText(titleText: String?) {
        titleView.text = titleText
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param color 文本颜色
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun setTitleTextColor(@ColorInt color: Int) {
        titleView.setTextColor(color)
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param colorRes 文本颜色
     */
    protected fun setTitleTextColorRes(@ColorRes colorRes: Int) {
        val color: Int = ContextCompat.getColor(this, colorRes)
        setTitleTextColor(color)
    }

    /**
     * 设置标题栏背景色
     *
     * @param color 标题栏背景色
     */
    protected fun setTitleBackgroundColor(@ColorInt color: Int) {
        toolbar.setBackgroundColor(color)
    }

    /**
     * 设置标题栏背景
     *
     * @param drawable 标题栏背景
     */
    protected fun setTitleBackgroundDrawable(drawable: Drawable?) {
        toolbar.background = drawable
    }

    /**
     * 设置标题栏背景
     *
     * @param drawableRes 标题栏背景
     */
    protected fun setTitleBackgroundResource(@DrawableRes drawableRes: Int) {
        toolbar.setBackgroundResource(drawableRes)
    }

    /**
     * 获取整个抽屉布局
     *
     * @return 整个抽屉布局
     */
    protected fun getDrawerLayout(): DrawerLayout {
        return findViewById(R.id.drawer_layout)
    }

    /**
     * 获取整个侧边栏
     *
     * @return 整个侧边栏
     */
    protected fun getNavigationView(): NavigationView {
        return findViewById(R.id.nav_view)
    }

    /**
     * 获取侧边栏的头部的资源id，如果不设置此选项，会以默认的头部布局进行填充
     * 可重写此方法设置自定义的侧边栏的头部的资源id
     *
     * @return 侧边栏的头部的资源id
     */
    @LayoutRes
    protected fun setNavigationViewHeaderViewLayoutResId(): Int {
        return 0
    }

    /**
     * DrawerLayout的状态改变了
     * 可重写此方法处理DrawerLayout的状态改变事件
     *
     * @param newState 新的状态
     */
    protected fun drawerStateChanged(@Suppress("UNUSED_PARAMETER") newState: Int) {

    }

    /**
     * DrawerLayout已经完全关闭了
     * 可重写此方法处理 DrawerLayout完全关闭的事件
     *
     * @param drawerView 侧边栏
     */
    protected fun drawerClosed(@Suppress("UNUSED_PARAMETER") drawerView: View) {

    }

    /**
     * DrawerLayout已经完全打开了
     * 可重写此方法处理 DrawerLayout完全打开的事件
     *
     * @param drawerView 侧边栏
     */
    protected fun drawerOpened(@Suppress("UNUSED_PARAMETER") drawerView: View) {

    }

    /**
     * DrawerLayout的滑动监听
     * 可重写此方法处理 DrawerLayout的滑动监听
     *
     * @param drawerView  侧边栏
     * @param slideOffset 滑动距离
     */
    protected fun drawerSlide(@Suppress("UNUSED_PARAMETER") drawerView: View, @Suppress("UNUSED_PARAMETER") slideOffset: Float) {

    }

    /**
     * 获取侧边栏的菜单的资源id，如果不设置此选项，会以默认的菜单进行填充
     * 可重写此方法设置自定义的侧边栏的菜单的资源id
     *
     * @return 侧边栏的菜单的资源id
     */
    @MenuRes
    protected fun setNavigationMenuResId(): Int {
        return 0
    }

    /**
     * 侧边栏选项被选中时执行的回调
     * 可重写此方法处理侧边栏选项被选中的事件
     *
     * @param menuItemId 被选中的侧边栏选项ID
     */
    protected fun navigationItemSelected(@Suppress("UNUSED_PARAMETER") @IdRes menuItemId: Int) {

    }
}
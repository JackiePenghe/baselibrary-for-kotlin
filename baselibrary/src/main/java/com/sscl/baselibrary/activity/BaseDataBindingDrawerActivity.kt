package com.sscl.baselibrary.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.google.android.material.navigation.NavigationView
import com.sscl.baselibrary.R

/**
 * 使用ViewBiding的带有侧边栏的Activity基类
 *
 * @author alm
 */
abstract class BaseDataBindingDrawerActivity<T : ViewDataBinding> : AppCompatActivity() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    /* * * * * * * * * * * * * * * * * * * 延时初始化属性 * * * * * * * * * * * * * * * * * * */

    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var binding: T

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
    /**
     * 获取Activity布局的整个布局
     *
     * @return Activity布局的整个布局
     */
    /**
     * 子类继承此类之后，设置的布局都在FrameLayout中
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var contentView: FrameLayout
        private set

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
                this@BaseDataBindingDrawerActivity,
                setNavigationViewHeaderViewLayoutResId(),
                null
            )
        } catch (e: Exception) {
            View.inflate(this@BaseDataBindingDrawerActivity, R.layout.nav_header_main, null)
        }
        navigationView.addHeaderView(headerView)
        try {
            navigationView.inflateMenu(setNavigationMenuResId())
        } catch (e: Exception) {
            navigationView.inflateMenu(R.menu.com_jackiepenghe_activity_main_drawer)
        }
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)
        contentView = findViewById(R.id.base_frame_content)
        binding = DataBindingUtil.inflate(layoutInflater, setLayout(), null, false)
        contentView.addView(binding.root)
        doBeforeInitOthers()
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

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 侧边栏选项被选中时执行的回调
     *
     * @param menuItemId 被选中的侧边栏选项ID
     */
    abstract fun navigationItemSelected(@IdRes menuItemId: Int)

    /**
     * 获取侧边栏的菜单的资源id，如果不设置此选项，会以默认的菜单进行填充
     *
     * @return 侧边栏的菜单的资源id
     */
    @MenuRes
    abstract fun setNavigationMenuResId(): Int

    /**
     * 获取侧边栏的头部的资源id，如果不设置此选项，会以默认的头部布局进行填充
     *
     * @return 侧边栏的头部的资源id
     */
    @LayoutRes
    abstract fun setNavigationViewHeaderViewLayoutResId(): Int

    /**
     * DrawerLayout的状态改变了
     *
     * @param newState 新的状态
     */
    abstract fun drawerStateChanged(newState: Int)

    /**
     * DrawerLayout已经完全关闭了
     *
     * @param drawerView 侧边栏
     */
    abstract fun drawerClosed(drawerView: View)

    /**
     * DrawerLayout已经完全打开了
     *
     * @param drawerView 侧边栏
     */
    abstract fun drawerOpened(drawerView: View)

    /**
     * DrawerLayout的滑动监听
     *
     * @param drawerView  侧边栏
     * @param slideOffset 滑动距离
     */
    abstract fun drawerSlide(drawerView: View, slideOffset: Float)

    /**
     * 在最后执行的操作
     */
    abstract fun doAfterAll()

    /**
     * 初始化事件
     */
    abstract fun initEvents()

    /**
     * 初始化其他数据
     */
    abstract fun initOtherData()

    /**
     * 初始化控件数据
     */
    abstract fun initViewData()

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    abstract fun doBeforeInitOthers()

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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * protected方法
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
}
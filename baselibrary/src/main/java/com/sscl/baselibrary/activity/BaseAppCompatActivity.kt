package com.sscl.baselibrary.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.sscl.baselibrary.R

/**
 * 基于AppCompatActivity的封装的Activity基类
 *
 * @author alm
 */
abstract class BaseAppCompatActivity : AppCompatActivity() {
    /*--------------------------------静态常量--------------------------------*/
    protected val TAG: String = javaClass.getSimpleName()
    /*--------------------------------成员变量--------------------------------*/
    /**
     * 标题
     */
    private var toolbar: Toolbar? = null

    /**
     * 标题的返回按钮处的点击事件
     */
    private var mTitleBackButtonOnClickListener: View.OnClickListener? =
        View.OnClickListener {
            val b: Boolean = titleBackClicked()
            if (!b) {
                finish()
            }
        }

    /**
     * 子类继承此类之后，设置的布局都在FrameLayout中
     */
    private var content: FrameLayout? = null

    /**
     * 标题栏的文字
     */
    private var titleView: TextView? = null
    /**
     * 获取此界面的根布局
     *
     * @return 此界面的根布局
     */
    /**
     * 整个BaseActivity的根布局（setContentLayout传入的布局）
     */
    protected var rootView: LinearLayout? = null
        private set

    /*--------------------------------重写父类方法--------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBeforeSetLayout()
        setContentView(R.layout.com_jackiepenghe_baselibrary_activity_base_appcompat)
        initThisView()
        initThisData()
        initThisEvents()
        doBeforeInitOthers()
        initViews()
        initViewData()
        initOtherData()
        initEvents()
        doAfterAll()
    }

    public override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //创建菜单选项
        return createOptionsMenu(menu)
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //菜单的选项被点击时的处理
        return optionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar!!.setNavigationOnClickListener(null)
        toolbar = null
        mTitleBackButtonOnClickListener = null
        content!!.removeAllViews()
        content = null
    }
    /*--------------------------------抽象方法--------------------------------*/
    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    protected abstract fun titleBackClicked(): Boolean

    /**
     * 在设置布局之前需要进行的操作
     */
    protected abstract fun doBeforeSetLayout()

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @LayoutRes
    protected abstract fun setLayout(): Int

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    protected abstract fun doBeforeInitOthers()

    /**
     * 初始化布局控件
     */
    protected abstract fun initViews()

    /**
     * 初始化控件数据
     */
    protected abstract fun initViewData()

    /**
     * 初始化其他数据
     */
    protected abstract fun initOtherData()

    /**
     * 初始化事件
     */
    protected abstract fun initEvents()

    /**
     * 在最后进行的操作
     */
    protected abstract fun doAfterAll()

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    protected abstract fun createOptionsMenu(menu: Menu): Boolean

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    protected abstract fun optionsItemSelected(item: MenuItem): Boolean
    /*--------------------------------私有方法--------------------------------*/
    /**
     * 初始化本类固定的控件
     */
    private fun initThisView() {
        toolbar = findViewById(R.id.toolbar)
        titleView = findViewById(R.id.toolbar_title)
        content = findViewById(R.id.base_frame_content)
        rootView = findViewById(R.id.base_root_view)
    }

    /**
     * 初始化本类固定的数据
     */
    private fun initThisData() {
        titleView!!.setText(R.string.com_jackiepenghe_app_title)
        setSupportActionBar(toolbar)
        val supportActionBar: ActionBar? = getSupportActionBar()
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true)
            //设置返回键可用
            supportActionBar.setHomeButtonEnabled(true)
            //            //不显示标题
            supportActionBar.setDisplayShowTitleEnabled(false)
        }
        val layoutResId: Int = setLayout()
        if (layoutResId == 0) {
            throw RuntimeException("setLayout with wrong layout resource id")
        }
        val view: View = getLayoutInflater().inflate(layoutResId, null)
        content!!.addView(view)
    }

    /**
     * 初始化本类固定的事件
     */
    private fun initThisEvents() {
        toolbar!!.setNavigationOnClickListener(mTitleBackButtonOnClickListener)
    }
    /*--------------------------------子类可用方法--------------------------------*/
    /**
     * 设置title返回按钮的处理事件
     *
     * @param titleBackButtonOnClickListener 返回按钮的处理事件
     */
    protected fun setTitleBackOnClickListener(titleBackButtonOnClickListener: View.OnClickListener?) {
        mTitleBackButtonOnClickListener = titleBackButtonOnClickListener
    }

    /**
     * 隐藏标题栏的返回按钮
     */
    protected fun hideTitleBackButton() {
        val supportActionBar: ActionBar? = getSupportActionBar()
        if (null == supportActionBar) {
            throw RuntimeException("toolbar is null!Please invoke this method after method \"setLayout()\"")
        }
        //左侧不添加默认的返回图标
        supportActionBar.setDisplayHomeAsUpEnabled(false)
        //设置返回键不可用
        getSupportActionBar()!!.setHomeButtonEnabled(false)
    }

    /**
     * 显示标题栏的返回按钮
     */
    protected fun showTitleBackButton() {
        val supportActionBar: ActionBar? = getSupportActionBar()
        if (null == supportActionBar) {
            throw RuntimeException("toolbar is null!Please invoke this method after method \"setLayout()\"")
        }
        //左侧添加一个默认的返回图标
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        //设置返回键可用
        getSupportActionBar()!!.setHomeButtonEnabled(true)
    }

    /**
     * 设置根布局（整个Activity）的背景色
     *
     * @param drawableRes 背景色
     */
    protected fun setRootBackGroundResource(@DrawableRes drawableRes: Int) {
        rootView!!.setBackgroundResource(drawableRes)
    }

    /**
     * 设置根布局（整个Activity）的背景色
     *
     * @param color 背景色
     */
    protected fun setRootBackGroundColor(@ColorInt color: Int) {
        rootView!!.setBackgroundColor(color)
    }

    /**
     * 隐藏标题栏
     */
    protected fun hideTitleBar() {
        val supportActionBar: ActionBar? = getSupportActionBar()
        if (null == supportActionBar) {
            throw RuntimeException("supportActionBar is null!Please invoke this method after method \"setLayout()\"")
        }
        supportActionBar.hide()
    }

    /**
     * 显示标题栏
     */
    protected fun showTitleBar() {
        val supportActionBar: ActionBar? = getSupportActionBar()
        if (supportActionBar != null) {
            supportActionBar.show()
        }
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleRes 标题栏的资源id
     */
    protected fun setTitleText(@StringRes titleRes: Int) {
        if (null == titleView) {
            throw RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"")
        }
        titleView!!.setText(titleRes)
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏的文本
     */
    protected fun setTitleText(titleText: String?) {
        if (null == titleView) {
            throw RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"")
        }
        titleView!!.setText(titleText)
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param color 文本颜色
     */
    protected fun setTitleTextColor(@ColorInt color: Int) {
        if (titleView == null) {
            throw RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"")
        }
        titleView!!.setTextColor(color)
    }

    /**
     * 设置标题栏的背景色
     *
     * @param color 标题栏的背景色
     */
    protected fun setTitleBackgroundColor(@ColorInt color: Int) {
        if (titleView == null) {
            throw RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"")
        }
        titleView!!.setBackgroundColor(color)
    }

    /**
     * 设置标题栏的背景
     *
     * @param drawable 标题栏的背景
     */
    protected fun setTitleBackgroundDrawable(drawable: Drawable?) {
        if (toolbar == null) {
            throw RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"")
        }
        toolbar!!.setBackground(drawable)
    }

    /**
     * 设置标题栏的背景
     *
     * @param drawableRes 标题栏的背景
     */
    protected fun setTitleBackgroundResource(@DrawableRes drawableRes: Int) {
        if (toolbar == null) {
            throw RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"")
        }
        toolbar!!.setBackgroundResource(drawableRes)
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
     * 设置标题栏左边的图片
     *
     * @param drawableId 标题栏左边的图片资源id
     */
    protected fun setTitleBackIcon(@DrawableRes drawableId: Int) {
        toolbar!!.setNavigationIcon(drawableId)
    }

    /**
     * 获取Activity布局的整个布局
     *
     * @return Activity布局的整个布局
     */
    protected val contentView: FrameLayout
        protected get() {
            return (content)!!
        }
}
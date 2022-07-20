package com.sscl.baselibrary.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sscl.baselibrary.R

/**
 * 基于AppCompatActivity的封装的Activity基类
 *
 * @author alm
 */
abstract class BaseDataBindingAppCompatActivity<B : ViewDataBinding> : AppCompatActivity() {

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    protected lateinit var binding: B

    /*--------------------------------重写父类方法--------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBeforeSetLayout()
        binding = DataBindingUtil.setContentView(this, setLayout())
        doBeforeInitOthers()
        initViews()
        initViewData()
        initOtherData()
        initEvents()
        doAfterAll()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //创建菜单选项
        return createOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //菜单的选项被点击时的处理
        return optionsItemSelected(item)
    }

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 在设置布局之前需要进行的操作
     */
    abstract fun doBeforeSetLayout()

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @LayoutRes
    abstract fun setLayout(): Int

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
     * 在最后进行的操作
     */
    abstract fun doAfterAll()

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    abstract fun createOptionsMenu(menu: Menu): Boolean

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    abstract fun optionsItemSelected(item: MenuItem): Boolean
}